package com.pharmacy.refill.repository;

import com.pharmacy.refill.entity.RefillOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RefillOrderRepository extends JpaRepository<RefillOrder, String> {
    
    List<RefillOrder> findByMemberIdOrderByOrderDateDesc(String memberId);
    
    Optional<RefillOrder> findTopByMemberIdOrderByOrderDateDesc(String memberId);
    
    List<RefillOrder> findByOrderStatus(RefillOrder.OrderStatus orderStatus);
    
    List<RefillOrder> findByOrderType(RefillOrder.OrderType orderType);
    
    List<RefillOrder> findByMemberIdAndOrderStatus(String memberId, RefillOrder.OrderStatus orderStatus);
    
    @Query("SELECT ro FROM RefillOrder ro WHERE ro.memberId = :memberId AND ro.orderDate >= :fromDate")
    List<RefillOrder> findByMemberIdAndOrderDateAfter(@Param("memberId") String memberId,
                                                      @Param("fromDate") LocalDateTime fromDate);
    
    @Query("SELECT ro FROM RefillOrder ro WHERE ro.orderDate BETWEEN :startDate AND :endDate")
    List<RefillOrder> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(ro) FROM RefillOrder ro WHERE ro.memberId = :memberId AND ro.orderStatus = :status")
    Long countByMemberIdAndOrderStatus(@Param("memberId") String memberId,
                                       @Param("status") RefillOrder.OrderStatus status);
}