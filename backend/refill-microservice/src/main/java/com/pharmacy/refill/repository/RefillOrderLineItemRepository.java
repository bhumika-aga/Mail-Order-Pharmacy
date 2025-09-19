package com.pharmacy.refill.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pharmacy.refill.entity.RefillOrderLineItem;

@Repository
public interface RefillOrderLineItemRepository extends JpaRepository<RefillOrderLineItem, Long> {

    List<RefillOrderLineItem> findByRefillOrderRefillOrderId(String refillOrderId);

    List<RefillOrderLineItem> findByDrugCode(String drugCode);

    List<RefillOrderLineItem> findByPrescriptionId(String prescriptionId);

    @Query("SELECT rli FROM RefillOrderLineItem rli WHERE rli.refillOrder.memberId = :memberId")
    List<RefillOrderLineItem> findByMemberId(@Param("memberId") String memberId);

    @Query("SELECT rli FROM RefillOrderLineItem rli WHERE rli.refillOrder.refillOrderId = :refillOrderId AND rli.drugCode = :drugCode")
    List<RefillOrderLineItem> findByRefillOrderIdAndDrugCode(@Param("refillOrderId") String refillOrderId,
            @Param("drugCode") String drugCode);

    @Query("SELECT SUM(rli.totalPrice) FROM RefillOrderLineItem rli WHERE rli.refillOrder.refillOrderId = :refillOrderId")
    Double calculateTotalOrderAmount(@Param("refillOrderId") String refillOrderId);
}