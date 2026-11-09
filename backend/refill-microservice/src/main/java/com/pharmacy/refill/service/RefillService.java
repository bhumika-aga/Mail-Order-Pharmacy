package com.pharmacy.refill.service;

import com.pharmacy.refill.client.DrugServiceClient;
import com.pharmacy.refill.client.SubscriptionServiceClient;
import com.pharmacy.refill.dto.*;
import com.pharmacy.refill.entity.PendingStockRelease;
import com.pharmacy.refill.entity.RefillOrder;
import com.pharmacy.refill.entity.RefillOrderLineItem;
import com.pharmacy.refill.exception.ApiException;
import com.pharmacy.refill.repository.RefillOrderLineItemRepository;
import com.pharmacy.refill.repository.RefillOrderRepository;
import com.pharmacy.refill.security.ErasedMembers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Refill orders. Methods that call other services are deliberately not
 * transactional: a database connection (the pool is small) must never be held
 * while waiting on the network.
 */
@Service
public class RefillService {
    
    private static final Logger log = LoggerFactory.getLogger(RefillService.class);
    
    private final RefillOrderRepository refillOrderRepository;
    private final RefillOrderLineItemRepository refillOrderLineItemRepository;
    private final SubscriptionServiceClient subscriptionServiceClient;
    private final DrugServiceClient drugServiceClient;
    private final StockReleaseService stockReleaseService;
    private final ErasedMembers erasedMembers;
    private final TransactionTemplate transactionTemplate;
    
    public RefillService(RefillOrderRepository refillOrderRepository,
                         RefillOrderLineItemRepository refillOrderLineItemRepository,
                         SubscriptionServiceClient subscriptionServiceClient, DrugServiceClient drugServiceClient,
                         StockReleaseService stockReleaseService, ErasedMembers erasedMembers,
                         PlatformTransactionManager transactionManager) {
        this.refillOrderRepository = refillOrderRepository;
        this.refillOrderLineItemRepository = refillOrderLineItemRepository;
        this.subscriptionServiceClient = subscriptionServiceClient;
        this.drugServiceClient = drugServiceClient;
        this.stockReleaseService = stockReleaseService;
        this.erasedMembers = erasedMembers;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }
    
    @Transactional(readOnly = true)
    public RefillStatusResponse getLatestRefillStatus(String memberId) {
        return refillOrderRepository.findTopByMemberIdOrderByOrderDateDesc(memberId)
                   .map(this::toStatusResponse)
                   .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Not Found",
                       "No refill orders found for this account"));
    }
    
    public List<RefillDueResponse> getRefillDuesAsOfDate(String token, LocalDate dueDate) {
        return subscriptionServiceClient.getRefillDuesAsOfDate(token, dueDate);
    }
    
    /**
     * Creates and confirms an adhoc refill order for the member who owns {@code token}.
     * <ol>
     *   <li>Every line must name one of the member's own prescriptions, for that drug.</li>
     *   <li>Stock and prices are checked for every line before anything is reserved.</li>
     *   <li>Stock is reserved line by line; if any reservation, or saving the order,
     *       fails, everything already reserved is released again.</li>
     * </ol>
     */
    public AdhocRefillResponse createAdhocRefillOrder(String token, AdhocRefillRequest request) {
        String location = request.getMemberLocation();
        List<AdhocRefillPrescriptionRequest> lines = request.getPrescriptions();
        
        verifyPrescriptions(token, lines);
        
        RefillOrder refillOrder = new RefillOrder(
            newOrderId(),
            request.getMemberId(),
            LocalDateTime.now(),
            location,
            RefillOrder.OrderStatus.PENDING,
            RefillOrder.OrderType.ADHOC,
            null); // adhoc orders are not tied to a subscription
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (AdhocRefillPrescriptionRequest line : lines) {
            if (!drugServiceClient.checkDrugAvailability(token, line.getDrugCode(), location, line.getQuantity())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Stock Unavailable",
                    "Drug " + line.getDrugCode() + " is not available in the required quantity at " + location);
            }
            Map<String, Object> drug = drugServiceClient.getDrugDetails(token, line.getDrugCode())
                                           .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,
                                               "Invalid Drug", "Drug " + line.getDrugCode() + " does not exist"));
            BigDecimal unitPrice = new BigDecimal(drug.get("costPerPackage").toString())
                                       .setScale(2, RoundingMode.HALF_UP);
            RefillOrderLineItem lineItem = new RefillOrderLineItem(
                line.getDrugCode(), (String) drug.get("drugName"), line.getQuantity(),
                line.getPrescriptionId(), unitPrice);
            refillOrder.addLineItem(lineItem);
            totalAmount = totalAmount.add(lineItem.getTotalPrice());
        }
        
        List<PendingStockRelease> reserved = new ArrayList<>();
        try {
            for (AdhocRefillPrescriptionRequest line : lines) {
                if (!drugServiceClient.reserveDrugStock(line.getDrugCode(), location, line.getQuantity())) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Stock Unavailable",
                        "Stock for drug " + line.getDrugCode() + " could not be reserved at " + location);
                }
                reserved.add(new PendingStockRelease(refillOrder.getRefillOrderId(), line.getDrugCode(), location,
                    line.getQuantity()));
            }
            // A single save (its own transaction) cascades the line items.
            refillOrder.setOrderStatus(RefillOrder.OrderStatus.CONFIRMED);
            refillOrderRepository.save(refillOrder);
        } catch (RuntimeException e) {
            // Compensation: return what was reserved (stored for retry if that fails too).
            stockReleaseService.send(reserved);
            throw e;
        }
        
        return new AdhocRefillResponse(
            refillOrder.getRefillOrderId(),
            request.getMemberId(),
            refillOrder.getOrderDate(),
            location,
            refillOrder.getOrderStatus().toString(),
            "Adhoc refill order created successfully",
            totalAmount);
    }
    
    /**
     * A short random order ID. save() on an existing ID would silently overwrite
     * that order, so a (rare) collision is retried instead.
     */
    private String newOrderId() {
        String id;
        do {
            id = "RO" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (refillOrderRepository.existsById(id));
        return id;
    }
    
    /**
     * Rejects lines whose prescription is not the member's, or is for a different drug.
     */
    private void verifyPrescriptions(String token, List<AdhocRefillPrescriptionRequest> lines) {
        Map<String, PrescriptionSummary> prescriptions = subscriptionServiceClient.getMemberPrescriptions(token)
                                                             .stream()
                                                             .collect(Collectors.toMap(
                                                                 PrescriptionSummary::prescriptionId,
                                                                 Function.identity(), (a, b) -> a));
        for (AdhocRefillPrescriptionRequest line : lines) {
            PrescriptionSummary prescription = prescriptions.get(line.getPrescriptionId());
            if (prescription == null) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid Prescription",
                    "Prescription " + line.getPrescriptionId() + " was not found for this member");
            }
            if (!line.getDrugCode().equals(prescription.drugId())) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid Prescription",
                    "Prescription " + line.getPrescriptionId() + " does not cover drug " + line.getDrugCode());
            }
        }
    }
    
    /**
     * Stores, in the current transaction, the release of the stock the order still
     * holds (none once it has shipped), so it can't be lost if sending it fails.
     */
    private List<PendingStockRelease> queueHeldStock(RefillOrder order) {
        if (!order.getOrderStatus().holdsStock()) {
            return List.of();
        }
        return stockReleaseService.queue(order.getLineItems().stream()
                                             .map(item -> new PendingStockRelease(order.getRefillOrderId(),
                                                 item.getDrugCode(), order.getMemberLocation(), item.getQuantity()))
                                             .toList());
    }
    
    /**
     * Moves an order along its lifecycle ({@link RefillOrder.OrderStatus#canMoveTo});
     * anything else, including setting the status it already has, is a 409.
     * Cancelling an order that still holds stock returns that stock.
     * <p>
     * The order row is locked while its status changes, so two concurrent requests
     * can't both apply (two cancellations would release the stock twice). The
     * release is stored with the status change and sent after it has committed,
     * without holding a database connection during the network calls; if the drugs
     * service is down it is retried until it succeeds ({@link StockReleaseService}).
     */
    public RefillStatusResponse updateRefillOrderStatus(String refillOrderId, RefillOrder.OrderStatus newStatus) {
        List<PendingStockRelease> toRelease = new ArrayList<>();
        RefillOrder order = transactionTemplate.execute(tx -> {
            RefillOrder locked = refillOrderRepository.findByIdForUpdate(refillOrderId)
                                     .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Not Found",
                                         "Refill order not found: " + refillOrderId));
            RefillOrder.OrderStatus current = locked.getOrderStatus();
            if (current == newStatus) {
                throw new ApiException(HttpStatus.CONFLICT, "Invalid Status Change",
                    "Refill order " + refillOrderId + " is already " + current);
            }
            if (!current.canMoveTo(newStatus)) {
                throw new ApiException(HttpStatus.CONFLICT, "Invalid Status Change",
                    "Refill order " + refillOrderId + " cannot change from " + current + " to " + newStatus);
            }
            if (newStatus == RefillOrder.OrderStatus.CANCELLED) {
                toRelease.addAll(queueHeldStock(locked));
            }
            
            locked.setOrderStatus(newStatus);
            if (newStatus == RefillOrder.OrderStatus.SHIPPED) {
                locked.setTrackingNumber("TRK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }
            if (newStatus == RefillOrder.OrderStatus.DELIVERED) {
                locked.setDeliveryDate(LocalDateTime.now());
            }
            return refillOrderRepository.save(locked);
        });
        
        stockReleaseService.send(toRelease);
        return toStatusResponse(order);
    }
    
    /**
     * Erases all of a member's refill orders and their line items (their account
     * is being deleted). The member's existing tokens are cut off first, so no new
     * order can slip in. Each order is locked and deleted in its own transaction,
     * together with the stored release of any stock it still held, which is then
     * sent. A repeated call is safe: deleted orders are gone, so no stock is
     * returned twice.
     *
     * @return the number of orders deleted
     */
    public int eraseMemberOrders(String memberId) {
        erasedMembers.record(memberId);
        int deleted = 0;
        for (String orderId : refillOrderRepository.findOrderIdsByMemberId(memberId)) {
            List<PendingStockRelease> toRelease = new ArrayList<>();
            RefillOrder order = transactionTemplate.execute(tx -> {
                RefillOrder locked = refillOrderRepository.findByIdForUpdate(orderId).orElse(null);
                if (locked != null) { // null: deleted concurrently
                    toRelease.addAll(queueHeldStock(locked));
                    refillOrderRepository.delete(locked); // line items cascade
                }
                return locked;
            });
            if (order != null) {
                stockReleaseService.send(toRelease);
                deleted++;
            }
        }
        log.info("Erased {} refill orders for member {}", deleted, memberId);
        return deleted;
    }
    
    @Transactional(readOnly = true)
    public List<RefillStatusResponse> getAllRefillOrdersForMember(String memberId) {
        return refillOrderRepository.findByMemberIdOrderByOrderDateDesc(memberId).stream()
                   .map(this::toStatusResponse)
                   .collect(Collectors.toList());
    }
    
    private RefillStatusResponse toStatusResponse(RefillOrder order) {
        List<RefillLineItemResponse> lineItems = refillOrderLineItemRepository
                                                     .findByRefillOrderRefillOrderId(order.getRefillOrderId())
                                                     .stream()
                                                     .map(this::convertToLineItemResponse)
                                                     .collect(Collectors.toList());
        BigDecimal totalAmount = refillOrderLineItemRepository.calculateTotalOrderAmount(order.getRefillOrderId());
        
        return new RefillStatusResponse(
            order.getRefillOrderId(),
            order.getMemberId(),
            order.getOrderDate(),
            order.getMemberLocation(),
            order.getOrderStatus(),
            order.getOrderType(),
            order.getSubscriptionId(),
            order.getDeliveryDate(),
            order.getTrackingNumber(),
            lineItems,
            totalAmount);
    }
    
    private RefillLineItemResponse convertToLineItemResponse(RefillOrderLineItem lineItem) {
        return new RefillLineItemResponse(
            lineItem.getLineItemId(),
            lineItem.getDrugCode(),
            lineItem.getDrugName(),
            lineItem.getQuantity(),
            lineItem.getPrescriptionId(),
            lineItem.getUnitPrice(),
            lineItem.getTotalPrice());
    }
}
