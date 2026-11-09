package com.pharmacy.refill.repository;

import com.pharmacy.refill.entity.RefillOrder;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefillOrderRepository extends JpaRepository<RefillOrder, String> {
    
    List<RefillOrder> findByMemberIdOrderByOrderDateDesc(String memberId);
    
    Optional<RefillOrder> findTopByMemberIdOrderByOrderDateDesc(String memberId);
    
    /**
     * Loads the order and locks its row until the transaction ends (SELECT ... FOR
     * UPDATE), so concurrent status changes are applied one after the other.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ro FROM RefillOrder ro WHERE ro.refillOrderId = :refillOrderId")
    Optional<RefillOrder> findByIdForUpdate(@Param("refillOrderId") String refillOrderId);
    
    @Query("SELECT ro.refillOrderId FROM RefillOrder ro WHERE ro.memberId = :memberId")
    List<String> findOrderIdsByMemberId(@Param("memberId") String memberId);
}