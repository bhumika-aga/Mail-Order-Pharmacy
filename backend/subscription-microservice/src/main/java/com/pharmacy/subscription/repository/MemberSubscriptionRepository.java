package com.pharmacy.subscription.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pharmacy.subscription.entity.MemberSubscription;

@Repository
public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, String> {
    List<MemberSubscription> findByMemberId(String memberId);

    List<MemberSubscription> findByMemberIdAndSubscriptionStatus(String memberId,
            MemberSubscription.SubscriptionStatus status);

    List<MemberSubscription> findByPrescriptionId(String prescriptionId);

    List<MemberSubscription> findBySubscriptionStatus(MemberSubscription.SubscriptionStatus status);
}