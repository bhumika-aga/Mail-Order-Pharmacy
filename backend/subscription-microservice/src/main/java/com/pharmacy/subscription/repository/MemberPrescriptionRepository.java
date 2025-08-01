package com.pharmacy.subscription.repository;

import com.pharmacy.subscription.entity.MemberPrescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberPrescriptionRepository extends JpaRepository<MemberPrescription, String> {
    List<MemberPrescription> findByMemberId(String memberId);
    List<MemberPrescription> findByDrugId(String drugId);
}