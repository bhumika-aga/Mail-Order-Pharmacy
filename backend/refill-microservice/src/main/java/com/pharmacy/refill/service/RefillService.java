package com.pharmacy.refill.service;

import com.pharmacy.refill.client.DrugServiceClient;
import com.pharmacy.refill.client.SubscriptionServiceClient;
import com.pharmacy.refill.dto.*;
import com.pharmacy.refill.entity.RefillOrder;
import com.pharmacy.refill.entity.RefillOrderLineItem;
import com.pharmacy.refill.repository.RefillOrderLineItemRepository;
import com.pharmacy.refill.repository.RefillOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RefillService {
    
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
    
    public RefillStatusResponse getLatestRefillStatus(String token, String memberId) {
        Optional<RefillOrder> latestOrder = refillOrderRepository
                                                .findTopByMemberIdOrderByOrderDateDesc(memberId);
        
        if (latestOrder.isEmpty()) {
            throw new RuntimeException("No refill orders found for member: " + memberId);
        }
        
        RefillOrder order = latestOrder.get();
        List<RefillOrderLineItem> lineItems = refillOrderLineItemRepository
                                                  .findByRefillOrderRefillOrderId(order.getRefillOrderId());
        
        List<RefillLineItemResponse> lineItemResponses = lineItems.stream()
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
            lineItemResponses,
            totalAmount);
    }
    
    public List<RefillDueResponse> getRefillDuesAsOfDate(String token, LocalDate dueDate) {
        return subscriptionServiceClient.getRefillDuesAsOfDate(token, dueDate);
    }
    
    public AdhocRefillResponse createAdhocRefillOrder(String token, AdhocRefillRequest request) {
        // Generate unique order ID
        String refillOrderId = "RO" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        // Create refill order
        RefillOrder refillOrder = new RefillOrder(
            refillOrderId,
            request.getMemberId(),
            LocalDateTime.now(),
            request.getMemberLocation(),
            RefillOrder.OrderStatus.PENDING,
            RefillOrder.OrderType.ADHOC,
            null // No subscription ID for adhoc orders
        );
        
        // Validate and create line items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (AdhocRefillPrescriptionRequest prescriptionRequest : request.getPrescriptions()) {
            // Check drug availability
            boolean isAvailable = drugServiceClient.checkDrugAvailability(
                token,
                prescriptionRequest.getDrugCode(),
                request.getMemberLocation(),
                prescriptionRequest.getQuantity());
            
            if (!isAvailable) {
                throw new RuntimeException("Drug " + prescriptionRequest.getDrugCode() +
                                               " is not available in required quantity at location " +
                                               request.getMemberLocation());
            }
            
            // Get drug details
            Map<String, Object> drugDetails = drugServiceClient.getDrugDetails(token,
                prescriptionRequest.getDrugCode());
            if (drugDetails == null) {
                throw new RuntimeException(
                    "Drug details not found for drug code: "
                        + prescriptionRequest.getDrugCode());
            }
            
            String drugName = (String) drugDetails.get("drugName");
            BigDecimal unitPrice = new BigDecimal(drugDetails.get("costPerPackage").toString())
                                       .setScale(2, RoundingMode.HALF_UP);
            
            // Create line item
            RefillOrderLineItem lineItem = new RefillOrderLineItem(
                prescriptionRequest.getDrugCode(),
                drugName,
                prescriptionRequest.getQuantity(),
                prescriptionRequest.getPrescriptionId(),
                unitPrice);
            
            refillOrder.addLineItem(lineItem);
            totalAmount = totalAmount.add(lineItem.getTotalPrice());
            
            // Reserve drug stock
            boolean reserved = drugServiceClient.reserveDrugStock(
                token,
                prescriptionRequest.getDrugCode(),
                request.getMemberLocation(),
                prescriptionRequest.getQuantity());
            
            if (!reserved) {
                throw new RuntimeException("Failed to reserve stock for drug: "
                                               + prescriptionRequest.getDrugCode());
            }
        }
        
        // All line items reserved successfully: confirm and persist once.
        // A single save avoids re-cascading the line-item inserts (a second
        // save() on the detached order would duplicate them).
        refillOrder.setOrderStatus(RefillOrder.OrderStatus.CONFIRMED);
        refillOrderRepository.save(refillOrder);
        
        return new AdhocRefillResponse(
            refillOrderId,
            request.getMemberId(),
            refillOrder.getOrderDate(),
            request.getMemberLocation(),
            refillOrder.getOrderStatus().toString(),
            "Adhoc refill order created successfully",
            totalAmount);
    }
    
    public RefillStatusResponse updateRefillOrderStatus(String refillOrderId, RefillOrder.OrderStatus newStatus) {
        Optional<RefillOrder> orderOptional = refillOrderRepository.findById(refillOrderId);
        
        if (orderOptional.isEmpty()) {
            throw new RuntimeException("Refill order not found: " + refillOrderId);
        }
        
        RefillOrder order = orderOptional.get();
        order.setOrderStatus(newStatus);
        
        if (newStatus == RefillOrder.OrderStatus.SHIPPED) {
            order.setTrackingNumber("TRK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }
        
        if (newStatus == RefillOrder.OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDateTime.now());
        }
        
        refillOrderRepository.save(order);
        
        List<RefillOrderLineItem> lineItems = refillOrderLineItemRepository
                                                  .findByRefillOrderRefillOrderId(refillOrderId);
        List<RefillLineItemResponse> lineItemResponses = lineItems.stream()
                                                             .map(this::convertToLineItemResponse)
                                                             .collect(Collectors.toList());
        
        BigDecimal totalAmount = refillOrderLineItemRepository.calculateTotalOrderAmount(refillOrderId);
        
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
            lineItemResponses,
            totalAmount);
    }
    
    public List<RefillStatusResponse> getAllRefillOrdersForMember(String memberId) {
        List<RefillOrder> orders = refillOrderRepository.findByMemberIdOrderByOrderDateDesc(memberId);
        
        return orders.stream()
                   .map(order -> {
                       List<RefillOrderLineItem> lineItems = refillOrderLineItemRepository
                                                                 .findByRefillOrderRefillOrderId(order.getRefillOrderId());
                       List<RefillLineItemResponse> lineItemResponses = lineItems.stream()
                                                                            .map(this::convertToLineItemResponse)
                                                                            .collect(Collectors.toList());
                       
                       BigDecimal totalAmount = refillOrderLineItemRepository
                                                    .calculateTotalOrderAmount(order.getRefillOrderId());
                       
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
                           lineItemResponses,
                           totalAmount);
                   })
                   .collect(Collectors.toList());
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