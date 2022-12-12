package com.cts.subscription.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cts.subscription.model.MemberPrescription;

@Repository
public interface MemberPrescriptionRepository extends JpaRepository<MemberPrescription, Long> {

}
