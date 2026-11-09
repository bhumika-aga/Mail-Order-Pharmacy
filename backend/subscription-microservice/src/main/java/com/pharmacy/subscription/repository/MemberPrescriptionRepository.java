package com.pharmacy.subscription.repository;

import com.pharmacy.subscription.entity.MemberPrescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberPrescriptionRepository extends JpaRepository<MemberPrescription, String> {
    List<MemberPrescription> findByMemberId(String memberId);
    
    @Modifying
    @Query("DELETE FROM MemberPrescription p WHERE p.memberId = :memberId")
    int deleteByMemberId(@Param("memberId") String memberId);
}
