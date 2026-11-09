package com.pharmacy.refill.repository;

import com.pharmacy.refill.entity.RefillOrderLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RefillOrderLineItemRepository extends JpaRepository<RefillOrderLineItem, Long> {
    
    List<RefillOrderLineItem> findByRefillOrderRefillOrderId(String refillOrderId);
    
    @Query("SELECT SUM(rli.totalPrice) FROM RefillOrderLineItem rli WHERE rli.refillOrder.refillOrderId = :refillOrderId")
    BigDecimal calculateTotalOrderAmount(@Param("refillOrderId") String refillOrderId);
}