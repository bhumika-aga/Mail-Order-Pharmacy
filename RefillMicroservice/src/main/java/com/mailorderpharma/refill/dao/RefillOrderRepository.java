package com.mailorderpharma.refill.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import com.mailorderpharma.refill.entity.RefillOrder;

/**JPA Repository which interacts with database*/
@Repository
public interface RefillOrderRepository extends JpaRepository<RefillOrder,Integer> {
	
	@Modifying
	@Query("delete from RefillOrder where subId=?1")
	public int deleteBySubscriptionId(long subscriptionId);
	
	@Query(value = "SELECT s FROM RefillOrder s WHERE subId = ?1")
	 List<RefillOrder> findBySubscriptionId(Long subId);

}
