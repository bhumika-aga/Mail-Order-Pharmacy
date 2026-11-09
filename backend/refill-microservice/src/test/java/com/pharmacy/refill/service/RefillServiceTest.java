package com.pharmacy.refill.service;

import com.pharmacy.refill.client.DrugServiceClient;
import com.pharmacy.refill.client.SubscriptionServiceClient;
import com.pharmacy.refill.dto.AdhocRefillPrescriptionRequest;
import com.pharmacy.refill.dto.AdhocRefillRequest;
import com.pharmacy.refill.dto.AdhocRefillResponse;
import com.pharmacy.refill.dto.PrescriptionSummary;
import com.pharmacy.refill.entity.PendingStockRelease;
import com.pharmacy.refill.entity.RefillOrder;
import com.pharmacy.refill.entity.RefillOrderLineItem;
import com.pharmacy.refill.exception.ApiException;
import com.pharmacy.refill.repository.PendingStockReleaseRepository;
import com.pharmacy.refill.repository.RefillOrderLineItemRepository;
import com.pharmacy.refill.repository.RefillOrderRepository;
import com.pharmacy.refill.security.ErasedMembers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RefillServiceTest {
    
    private static final String TOKEN = "member-token";
    private static final String NY = "New York";
    
    private RefillOrderRepository orderRepository;
    private SubscriptionServiceClient subscriptionClient;
    private DrugServiceClient drugClient;
    private PendingStockReleaseRepository pendingReleases;
    private ErasedMembers erasedMembers;
    private RefillService service;
    
    private static AdhocRefillRequest order(AdhocRefillPrescriptionRequest... lines) {
        return new AdhocRefillRequest("MEM001", NY, List.of(lines));
    }
    
    private static AdhocRefillPrescriptionRequest line(String prescriptionId, String drugCode, int quantity) {
        return new AdhocRefillPrescriptionRequest(prescriptionId, drugCode, quantity);
    }
    
    private static void assertConflict(Runnable change) {
        assertThatThrownBy(change::run).isInstanceOfSatisfying(ApiException.class,
            e -> assertThat(e.getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }
    
    @BeforeEach
    void setUp() {
        orderRepository = mock(RefillOrderRepository.class);
        subscriptionClient = mock(SubscriptionServiceClient.class);
        drugClient = mock(DrugServiceClient.class);
        pendingReleases = mock(PendingStockReleaseRepository.class);
        when(pendingReleases.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
        erasedMembers = mock(ErasedMembers.class);
        service = new RefillService(orderRepository, mock(RefillOrderLineItemRepository.class),
            subscriptionClient, drugClient, new StockReleaseService(pendingReleases, drugClient), erasedMembers,
            mock(PlatformTransactionManager.class));
        when(orderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        
        // member MEM001 holds PRES001 (D001) and PRES002 (D002)
        when(subscriptionClient.getMemberPrescriptions(TOKEN)).thenReturn(List.of(
            new PrescriptionSummary("PRES001", "MEM001", "D001"),
            new PrescriptionSummary("PRES002", "MEM001", "D002")));
        when(drugClient.checkDrugAvailability(eq(TOKEN), anyString(), eq(NY), anyInt())).thenReturn(true);
        when(drugClient.getDrugDetails(eq(TOKEN), anyString())).thenAnswer(inv -> Optional.of(
            Map.of("drugName", "Drug " + inv.getArgument(1), "costPerPackage", 10.5)));
        when(drugClient.reserveDrugStock(anyString(), eq(NY), anyInt())).thenReturn(true);
        when(drugClient.releaseDrugStock(anyString(), anyString(), eq(NY), anyInt())).thenReturn(true);
    }
    
    @Test
    void confirmsAndSavesOrderWhenEverythingIsValid() {
        AdhocRefillResponse response = service.createAdhocRefillOrder(TOKEN,
            order(line("PRES001", "D001", 2), line("PRES002", "D002", 1)));
        
        ArgumentCaptor<RefillOrder> saved = ArgumentCaptor.forClass(RefillOrder.class);
        verify(orderRepository).save(saved.capture());
        assertThat(saved.getValue().getOrderStatus()).isEqualTo(RefillOrder.OrderStatus.CONFIRMED);
        assertThat(saved.getValue().getLineItems()).hasSize(2);
        assertThat(response.getTotalAmount()).isEqualByComparingTo(new BigDecimal("31.50"));
        verify(drugClient, never()).releaseDrugStock(anyString(), anyString(), anyString(), anyInt());
    }
    
    @Test
    void rejectsPrescriptionThatIsNotTheMembers() {
        assertThatThrownBy(() -> service.createAdhocRefillOrder(TOKEN, order(line("PRES999", "D001", 1))))
            .isInstanceOfSatisfying(ApiException.class, e -> {
                assertThat(e.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(e.getMessage()).contains("PRES999");
            });
        verify(drugClient, never()).reserveDrugStock(anyString(), anyString(), anyInt());
        verify(orderRepository, never()).save(any());
    }
    
    @Test
    void rejectsDrugThatThePrescriptionDoesNotCover() {
        // PRES001 is for D001, not D005
        assertThatThrownBy(() -> service.createAdhocRefillOrder(TOKEN, order(line("PRES001", "D005", 1))))
            .isInstanceOfSatisfying(ApiException.class,
                e -> assertThat(e.getMessage()).contains("does not cover drug D005"));
        verify(drugClient, never()).reserveDrugStock(anyString(), anyString(), anyInt());
    }
    
    @Test
    void releasesEarlierReservationsWhenALaterOneFails() {
        when(drugClient.reserveDrugStock("D002", NY, 1)).thenReturn(false);
        
        assertThatThrownBy(() -> service.createAdhocRefillOrder(TOKEN,
            order(line("PRES001", "D001", 2), line("PRES002", "D002", 1))))
            .isInstanceOfSatisfying(ApiException.class,
                e -> assertThat(e.getError()).isEqualTo("Stock Unavailable"));
        
        verify(drugClient).releaseDrugStock(anyString(), eq("D001"), eq(NY), eq(2));           // the line that was reserved
        verify(drugClient, never()).releaseDrugStock(anyString(), eq("D002"), anyString(), anyInt()); // never reserved
        verify(orderRepository, never()).save(any());
    }
    
    @Test
    void releasesAllReservationsWhenSavingTheOrderFails() {
        when(orderRepository.save(any())).thenThrow(new IllegalStateException("database down"));
        
        assertThatThrownBy(() -> service.createAdhocRefillOrder(TOKEN,
            order(line("PRES001", "D001", 2), line("PRES002", "D002", 1))))
            .isInstanceOf(IllegalStateException.class);
        
        verify(drugClient).releaseDrugStock(anyString(), eq("D001"), eq(NY), eq(2));
        verify(drugClient).releaseDrugStock(anyString(), eq("D002"), eq(NY), eq(1));
    }
    
    // --- order status lifecycle -------------------------------------------------
    
    @Test
    void reservesNothingWhenAnyLineIsOutOfStock() {
        when(drugClient.checkDrugAvailability(TOKEN, "D002", NY, 1)).thenReturn(false);
        
        assertThatThrownBy(() -> service.createAdhocRefillOrder(TOKEN,
            order(line("PRES001", "D001", 2), line("PRES002", "D002", 1))))
            .isInstanceOf(ApiException.class);
        verify(drugClient, never()).reserveDrugStock(anyString(), anyString(), anyInt());
    }
    
    /**
     * A stored order RO1 for MEM001 in New York: 2 x D001 and 1 x D002.
     */
    private RefillOrder storedOrder(String id, RefillOrder.OrderStatus status) {
        RefillOrder order = new RefillOrder(id, "MEM001", LocalDateTime.now(), NY, status,
            RefillOrder.OrderType.ADHOC, null);
        order.addLineItem(new RefillOrderLineItem("D001", "Drug D001", 2, "PRES001", new BigDecimal("10.50")));
        order.addLineItem(new RefillOrderLineItem("D002", "Drug D002", 1, "PRES002", new BigDecimal("10.50")));
        when(orderRepository.findByIdForUpdate(id)).thenReturn(Optional.of(order));
        return order;
    }
    
    @Test
    void cancellingAConfirmedOrderReturnsItsStock() {
        RefillOrder order = storedOrder("RO1", RefillOrder.OrderStatus.CONFIRMED);
        
        service.updateRefillOrderStatus("RO1", RefillOrder.OrderStatus.CANCELLED);
        
        assertThat(order.getOrderStatus()).isEqualTo(RefillOrder.OrderStatus.CANCELLED);
        verify(drugClient).releaseDrugStock(anyString(), eq("D001"), eq(NY), eq(2));
        verify(drugClient).releaseDrugStock(anyString(), eq("D002"), eq(NY), eq(1));
    }
    
    @Test
    void cancellingTwiceReturnsTheStockOnlyOnce() {
        storedOrder("RO1", RefillOrder.OrderStatus.IN_PROGRESS);
        service.updateRefillOrderStatus("RO1", RefillOrder.OrderStatus.CANCELLED);
        
        assertConflict(() -> service.updateRefillOrderStatus("RO1", RefillOrder.OrderStatus.CANCELLED));
        verify(drugClient, times(1)).releaseDrugStock(anyString(), eq("D001"), eq(NY), eq(2));
    }
    
    @Test
    void shippedOrdersCannotBeCancelledAndKeepTheirStockUsed() {
        storedOrder("RO1", RefillOrder.OrderStatus.SHIPPED);
        
        assertConflict(() -> service.updateRefillOrderStatus("RO1", RefillOrder.OrderStatus.CANCELLED));
        verify(drugClient, never()).releaseDrugStock(anyString(), anyString(), anyString(), anyInt());
    }
    
    @Test
    void statusCannotMoveBackwards() {
        RefillOrder order = storedOrder("RO1", RefillOrder.OrderStatus.DELIVERED);
        
        assertConflict(() -> service.updateRefillOrderStatus("RO1", RefillOrder.OrderStatus.PENDING));
        assertThat(order.getOrderStatus()).isEqualTo(RefillOrder.OrderStatus.DELIVERED);
    }
    
    @Test
    void shippingTwiceKeepsTheFirstTrackingNumber() {
        RefillOrder order = storedOrder("RO1", RefillOrder.OrderStatus.CONFIRMED);
        service.updateRefillOrderStatus("RO1", RefillOrder.OrderStatus.SHIPPED);
        String tracking = order.getTrackingNumber();
        assertThat(tracking).startsWith("TRK");
        
        assertConflict(() -> service.updateRefillOrderStatus("RO1", RefillOrder.OrderStatus.SHIPPED));
        assertThat(order.getTrackingNumber()).isEqualTo(tracking);
    }
    
    @Test
    void ordersMoveForwardThroughTheFullLifecycle() {
        RefillOrder order = storedOrder("RO1", RefillOrder.OrderStatus.CONFIRMED);
        service.updateRefillOrderStatus("RO1", RefillOrder.OrderStatus.IN_PROGRESS);
        service.updateRefillOrderStatus("RO1", RefillOrder.OrderStatus.SHIPPED);
        service.updateRefillOrderStatus("RO1", RefillOrder.OrderStatus.DELIVERED);
        
        assertThat(order.getOrderStatus()).isEqualTo(RefillOrder.OrderStatus.DELIVERED);
        assertThat(order.getDeliveryDate()).isNotNull();
        verify(drugClient, never()).releaseDrugStock(anyString(), anyString(), anyString(), anyInt());
    }
    
    // --- member erasure -------------------------------------------------------
    
    @Test
    void erasingAMemberDeletesEveryOrderAndReturnsStockOnlyForUnshippedOnes() {
        RefillOrder open = storedOrder("RO1", RefillOrder.OrderStatus.CONFIRMED);
        RefillOrder delivered = storedOrder("RO2", RefillOrder.OrderStatus.DELIVERED);
        RefillOrder cancelled = storedOrder("RO3", RefillOrder.OrderStatus.CANCELLED);
        when(orderRepository.findOrderIdsByMemberId("MEM001")).thenReturn(List.of("RO1", "RO2", "RO3"));
        
        assertThat(service.eraseMemberOrders("MEM001")).isEqualTo(3);
        
        verify(orderRepository).delete(open);
        verify(orderRepository).delete(delivered);
        verify(orderRepository).delete(cancelled);
        // only RO1 still held stock
        verify(drugClient, times(1)).releaseDrugStock(anyString(), eq("D001"), eq(NY), eq(2));
        verify(drugClient, times(1)).releaseDrugStock(anyString(), eq("D002"), eq(NY), eq(1));
    }
    
    @Test
    void erasingAMemberWithNoOrdersIsANoOp() {
        when(orderRepository.findOrderIdsByMemberId("MEM404")).thenReturn(List.of());
        
        assertThat(service.eraseMemberOrders("MEM404")).isZero();
        verify(orderRepository, never()).delete(any());
    }
    
    // --- reliable stock release ---------------------------------------------------
    
    @Test
    void cancellingStoresTheReleaseWithTheStatusChangeAndRemovesItOnceSent() {
        storedOrder("RO1", RefillOrder.OrderStatus.CONFIRMED);
        
        service.updateRefillOrderStatus("RO1", RefillOrder.OrderStatus.CANCELLED);
        
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<PendingStockRelease>> queued = ArgumentCaptor.forClass(List.class);
        verify(pendingReleases).saveAll(queued.capture());
        assertThat(queued.getValue()).extracting(PendingStockRelease::getDrugCode).containsExactly("D001", "D002");
        // each release is sent with its own ID, then removed from the queue
        for (PendingStockRelease release : queued.getValue()) {
            verify(drugClient).releaseDrugStock(release.getReleaseId(), release.getDrugCode(), NY, release.getQuantity());
            verify(pendingReleases).deleteById(release.getReleaseId());
        }
    }
    
    @Test
    void aReleaseThatFailsStaysQueuedAndIsRetriedWithTheSameId() {
        storedOrder("RO1", RefillOrder.OrderStatus.CONFIRMED);
        when(drugClient.releaseDrugStock(anyString(), anyString(), anyString(), anyInt()))
            .thenThrow(new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", "drugs service down"));
        
        // the cancellation itself still succeeds
        service.updateRefillOrderStatus("RO1", RefillOrder.OrderStatus.CANCELLED);
        
        ArgumentCaptor<PendingStockRelease> failed = ArgumentCaptor.forClass(PendingStockRelease.class);
        verify(pendingReleases, times(2)).save(failed.capture());
        verify(pendingReleases, never()).deleteById(anyString());
        assertThat(failed.getAllValues()).allSatisfy(r -> {
            assertThat(r.getAttempts()).isEqualTo(1);
            assertThat(r.getLastError()).contains("drugs service down");
        });
        
        // the drugs service is back: the scheduled retry sends the same releases and clears them
        reset(drugClient);
        when(drugClient.releaseDrugStock(anyString(), anyString(), anyString(), anyInt())).thenReturn(true);
        when(pendingReleases.findTop100ByOrderByCreatedAtAsc()).thenReturn(failed.getAllValues());
        new StockReleaseService(pendingReleases, drugClient).retryPending();
        
        for (PendingStockRelease release : failed.getAllValues()) {
            verify(drugClient).releaseDrugStock(release.getReleaseId(), release.getDrugCode(), NY, release.getQuantity());
            verify(pendingReleases).deleteById(release.getReleaseId());
        }
    }
    
    @Test
    void failedCompensationAfterAFailedOrderIsQueuedForRetry() {
        when(drugClient.reserveDrugStock("D002", NY, 1)).thenReturn(false);
        when(drugClient.releaseDrugStock(anyString(), eq("D001"), eq(NY), eq(2)))
            .thenThrow(new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", "drugs service down"));
        
        assertThatThrownBy(() -> service.createAdhocRefillOrder(TOKEN,
            order(line("PRES001", "D001", 2), line("PRES002", "D002", 1))))
            .isInstanceOfSatisfying(ApiException.class, e -> assertThat(e.getError()).isEqualTo("Stock Unavailable"));
        
        ArgumentCaptor<PendingStockRelease> queued = ArgumentCaptor.forClass(PendingStockRelease.class);
        verify(pendingReleases).save(queued.capture());
        assertThat(queued.getValue().getDrugCode()).isEqualTo("D001");
        assertThat(queued.getValue().getQuantity()).isEqualTo(2);
    }
    
    @Test
    void erasingAMemberCutsOffTheirTokensBeforeDeletingAnything() {
        storedOrder("RO1", RefillOrder.OrderStatus.DELIVERED);
        when(orderRepository.findOrderIdsByMemberId("MEM001")).thenReturn(List.of("RO1"));
        
        service.eraseMemberOrders("MEM001");
        
        var order = inOrder(erasedMembers, orderRepository);
        order.verify(erasedMembers).record("MEM001");
        order.verify(orderRepository).delete(any());
    }
}
