package com.pharmacy.refill.service;

import com.pharmacy.refill.client.DrugServiceClient;
import com.pharmacy.refill.client.SubscriptionServiceClient;
import com.pharmacy.refill.dto.AdhocRefillPrescriptionRequest;
import com.pharmacy.refill.dto.AdhocRefillRequest;
import com.pharmacy.refill.dto.AdhocRefillResponse;
import com.pharmacy.refill.dto.PrescriptionSummary;
import com.pharmacy.refill.entity.RefillOrder;
import com.pharmacy.refill.exception.ApiException;
import com.pharmacy.refill.repository.RefillOrderLineItemRepository;
import com.pharmacy.refill.repository.RefillOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RefillServiceTest {

    private static final String TOKEN = "member-token";
    private static final String NY = "New York";

    private RefillOrderRepository orderRepository;
    private SubscriptionServiceClient subscriptionClient;
    private DrugServiceClient drugClient;
    private RefillService service;

    @BeforeEach
    void setUp() {
        orderRepository = mock(RefillOrderRepository.class);
        subscriptionClient = mock(SubscriptionServiceClient.class);
        drugClient = mock(DrugServiceClient.class);
        service = new RefillService(orderRepository, mock(RefillOrderLineItemRepository.class),
            subscriptionClient, drugClient);

        // member MEM001 holds PRES001 (D001) and PRES002 (D002)
        when(subscriptionClient.getMemberPrescriptions(TOKEN)).thenReturn(List.of(
            new PrescriptionSummary("PRES001", "MEM001", "D001"),
            new PrescriptionSummary("PRES002", "MEM001", "D002")));
        when(drugClient.checkDrugAvailability(eq(TOKEN), anyString(), eq(NY), anyInt())).thenReturn(true);
        when(drugClient.getDrugDetails(eq(TOKEN), anyString())).thenAnswer(inv -> Optional.of(
            Map.of("drugName", "Drug " + inv.getArgument(1), "costPerPackage", 10.5)));
        when(drugClient.reserveDrugStock(anyString(), eq(NY), anyInt())).thenReturn(true);
        when(drugClient.releaseDrugStock(anyString(), eq(NY), anyInt())).thenReturn(true);
    }

    private static AdhocRefillRequest order(AdhocRefillPrescriptionRequest... lines) {
        return new AdhocRefillRequest("MEM001", NY, List.of(lines));
    }

    private static AdhocRefillPrescriptionRequest line(String prescriptionId, String drugCode, int quantity) {
        return new AdhocRefillPrescriptionRequest(prescriptionId, drugCode, quantity);
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
        verify(drugClient, never()).releaseDrugStock(anyString(), anyString(), anyInt());
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

        verify(drugClient).releaseDrugStock("D001", NY, 2);           // the line that was reserved
        verify(drugClient, never()).releaseDrugStock(eq("D002"), anyString(), anyInt()); // never reserved
        verify(orderRepository, never()).save(any());
    }

    @Test
    void releasesAllReservationsWhenSavingTheOrderFails() {
        when(orderRepository.save(any())).thenThrow(new IllegalStateException("database down"));

        assertThatThrownBy(() -> service.createAdhocRefillOrder(TOKEN,
            order(line("PRES001", "D001", 2), line("PRES002", "D002", 1))))
            .isInstanceOf(IllegalStateException.class);

        verify(drugClient).releaseDrugStock("D001", NY, 2);
        verify(drugClient).releaseDrugStock("D002", NY, 1);
    }

    @Test
    void reservesNothingWhenAnyLineIsOutOfStock() {
        when(drugClient.checkDrugAvailability(TOKEN, "D002", NY, 1)).thenReturn(false);

        assertThatThrownBy(() -> service.createAdhocRefillOrder(TOKEN,
            order(line("PRES001", "D001", 2), line("PRES002", "D002", 1))))
            .isInstanceOf(ApiException.class);
        verify(drugClient, never()).reserveDrugStock(anyString(), anyString(), anyInt());
    }
}
