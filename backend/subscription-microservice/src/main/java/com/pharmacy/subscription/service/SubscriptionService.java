package com.pharmacy.subscription.service;

import com.pharmacy.subscription.client.DrugServiceClient;
import com.pharmacy.subscription.dto.SubscriptionRequest;
import com.pharmacy.subscription.entity.MemberPrescription;
import com.pharmacy.subscription.entity.MemberSubscription;
import com.pharmacy.subscription.repository.MemberPrescriptionRepository;
import com.pharmacy.subscription.repository.MemberSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SubscriptionService {
    
    @Autowired
    private MemberPrescriptionRepository prescriptionRepository;
    
    @Autowired
    private MemberSubscriptionRepository subscriptionRepository;
    
    @Autowired
    private DrugServiceClient drugServiceClient;
    
    @Transactional
    public MemberSubscription subscribe(SubscriptionRequest request, String memberId, String token) {
        if (!drugServiceClient.verifyDrugAvailability(request.getDrugId(), request.getMemberLocation(), token)) {
            throw new RuntimeException("Drug not available at specified location");
        }
        
        String prescriptionId = "PRESC_" + UUID.randomUUID().toString().substring(0, 8);
        String subscriptionId = "SUB_" + UUID.randomUUID().toString().substring(0, 8);
        
        MemberPrescription prescription = new MemberPrescription(
            prescriptionId,
            memberId,
            request.getInsurancePolicyNumber(),
            request.getInsuranceProvider(),
            request.getPrescriptionDate(),
            request.getDrugId(),
            request.getDosagePerDay(),
            request.getPrescriptionCourse(),
            request.getDoctorDetails()
        );
        
        prescriptionRepository.save(prescription);
        
        MemberSubscription subscription = new MemberSubscription(
            subscriptionId,
            memberId,
            LocalDate.now(),
            prescriptionId,
            request.getRefillFrequency(),
            request.getMemberLocation(),
            MemberSubscription.SubscriptionStatus.ACTIVE
        );
        
        return subscriptionRepository.save(subscription);
    }
    
    @Transactional
    public boolean unsubscribe(String subscriptionId, String memberId) {
        Optional<MemberSubscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        
        if (subscriptionOpt.isPresent()) {
            MemberSubscription subscription = subscriptionOpt.get();
            
            if (!subscription.getMemberId().equals(memberId)) {
                throw new RuntimeException("Subscription does not belong to member");
            }
            
            if (subscription.getSubscriptionStatus() == MemberSubscription.SubscriptionStatus.INACTIVE) {
                throw new RuntimeException("Subscription is already inactive");
            }
            
            subscription.setSubscriptionStatus(MemberSubscription.SubscriptionStatus.INACTIVE);
            subscriptionRepository.save(subscription);
            return true;
        }
        
        return false;
    }
    
    public List<MemberSubscription> getMemberSubscriptions(String memberId) {
        return subscriptionRepository.findByMemberId(memberId);
    }
    
    public List<MemberSubscription> getActiveSubscriptions(String memberId) {
        return subscriptionRepository.findByMemberIdAndSubscriptionStatus(
            memberId, MemberSubscription.SubscriptionStatus.ACTIVE);
    }
    
    public Optional<MemberSubscription> getSubscriptionById(String subscriptionId) {
        return subscriptionRepository.findById(subscriptionId);
    }
    
    public List<MemberPrescription> getMemberPrescriptions(String memberId) {
        return prescriptionRepository.findByMemberId(memberId);
    }
}