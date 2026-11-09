package com.pharmacy.refill.service;

import com.pharmacy.refill.client.DrugServiceClient;
import com.pharmacy.refill.client.SubscriptionServiceClient;
import com.pharmacy.refill.dto.*;
import com.pharmacy.refill.entity.RefillOrder;
import com.pharmacy.refill.entity.RefillOrderLineItem;
import com.pharmacy.refill.exception.ApiException;
import com.pharmacy.refill.repository.RefillOrderLineItemRepository;
import com.pharmacy.refill.repository.RefillOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    
    public RefillService(RefillOrderRepository refillOrderRepository,
                         RefillOrderLineItemRepository refillOrderLineItemRepository,
                         SubscriptionServiceClient subscriptionServiceClient, DrugServiceClient drugServiceClient) {
        this.refillOrderRepository = refillOrderRepository;
        this.refillOrderLineItemRepository = refillOrderLineItemRepository;
        this.subscriptionServiceClient = subscriptionServiceClient;
        this.drugServiceClient = drugServiceClient;
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
        
        List<AdhocRefillPrescriptionRequest> reserved = new ArrayList<>();
        try {
            for (AdhocRefillPrescriptionRequest line : lines) {
                if (!drugServiceClient.reserveDrugStock(line.getDrugCode(), location, line.getQuantity())) {
                    throw new ApiException(HttpStatus.BAD_REQUEST, "Stock Unavailable",
                        "Stock for drug " + line.getDrugCode() + " could not be reserved at " + location);
                }
                reserved.add(line);
            }
            // A single save (its own transaction) cascades the line items.
            refillOrder.setOrderStatus(RefillOrder.OrderStatus.CONFIRMED);
            refillOrderRepository.save(refillOrder);
        } catch (RuntimeException e) {
            releaseReservations(reserved, location, refillOrder.getRefillOrderId());
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
     * Best-effort compensation: returns stock reserved for an order that could not
     * be completed. A release that fails is logged for manual correction rather
     * than hiding the original error.
     */
    private void releaseReservations(List<AdhocRefillPrescriptionRequest> reserved, String location, String orderId) {
        for (AdhocRefillPrescriptionRequest line : reserved) {
            try {
                if (!drugServiceClient.releaseDrugStock(line.getDrugCode(), location, line.getQuantity())) {
                    log.error("Stock release refused for order {}: {} x{} at {}; manual correction needed",
                        orderId, line.getDrugCode(), line.getQuantity(), location);
                }
            } catch (RuntimeException releaseError) {
                log.error("Stock release failed for order {}: {} x{} at {}; manual correction needed",
                    orderId, line.getDrugCode(), line.getQuantity(), location, releaseError);
            }
        }
    }
    
    @Transactional
    public RefillStatusResponse updateRefillOrderStatus(String refillOrderId, RefillOrder.OrderStatus newStatus) {
        RefillOrder order = refillOrderRepository.findById(refillOrderId)
                                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Not Found",
                                    "Refill order not found: " + refillOrderId));
        order.setOrderStatus(newStatus);
        
        if (newStatus == RefillOrder.OrderStatus.SHIPPED) {
            order.setTrackingNumber("TRK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        if (newStatus == RefillOrder.OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDateTime.now());
        }
        
        refillOrderRepository.save(order);
        return toStatusResponse(order);
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
