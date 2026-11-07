package com.pharmacy.subscription.repository;

import com.pharmacy.subscription.entity.MemberSubscription;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Queries whose results are returned by the API load the prescription eagerly
 * ({@code @EntityGraph}): open-in-view is disabled, so a lazy association would
 * fail when the response is serialized after the transaction has ended.
 */
@Repository
public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, String> {

    @EntityGraph(attributePaths = "prescription")
    List<MemberSubscription> findByMemberId(String memberId);

    @EntityGraph(attributePaths = "prescription")
    List<MemberSubscription> findByMemberIdAndSubscriptionStatus(String memberId,
                                                                 MemberSubscription.SubscriptionStatus status);

    @Override
    @EntityGraph(attributePaths = "prescription")
    Optional<MemberSubscription> findById(String subscriptionId);

    List<MemberSubscription> findByPrescriptionId(String prescriptionId);

    List<MemberSubscription> findBySubscriptionStatus(MemberSubscription.SubscriptionStatus status);
}
