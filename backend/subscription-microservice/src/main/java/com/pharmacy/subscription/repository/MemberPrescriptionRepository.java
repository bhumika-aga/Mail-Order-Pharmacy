package com.pharmacy.subscription.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pharmacy.subscription.entity.MemberPrescription;

@Repository
public interface MemberPrescriptionRepository extends JpaRepository<MemberPrescription, String> {
    List<MemberPrescription> findByMemberId(String memberId);

    List<MemberPrescription> findByDrugId(String drugId);
}