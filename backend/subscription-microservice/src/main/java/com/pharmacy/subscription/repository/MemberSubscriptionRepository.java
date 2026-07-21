package com.pharmacy.subscription.repository;

import com.pharmacy.subscription.entity.MemberSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, String> {
    List<MemberSubscription> findByMemberId(String memberId);
    
    List<MemberSubscription> findByMemberIdAndSubscriptionStatus(String memberId,
                                                                 MemberSubscription.SubscriptionStatus status);
    
    List<MemberSubscription> findByPrescriptionId(String prescriptionId);
    
    List<MemberSubscription> findBySubscriptionStatus(MemberSubscription.SubscriptionStatus status);
}