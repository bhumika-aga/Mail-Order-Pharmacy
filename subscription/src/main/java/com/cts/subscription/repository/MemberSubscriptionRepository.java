package com.cts.subscription.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.cts.subscription.model.MemberSubscription;


@Repository
public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, Long> {
	/**
	 * 
	 * @param mId
	 * @return
	 */
	@Query(value = "SELECT s FROM MemberSubscription s WHERE MEMBER_ID = ?1")
	 List<MemberSubscription> findByMemberId(String mId);

}
