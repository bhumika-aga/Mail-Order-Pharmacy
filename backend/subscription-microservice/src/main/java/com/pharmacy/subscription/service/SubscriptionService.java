package com.pharmacy.subscription.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pharmacy.subscription.client.DrugServiceClient;
import com.pharmacy.subscription.dto.SubscriptionRequest;
import com.pharmacy.subscription.entity.MemberPrescription;
import com.pharmacy.subscription.entity.MemberSubscription;
import com.pharmacy.subscription.repository.MemberPrescriptionRepository;
import com.pharmacy.subscription.repository.MemberSubscriptionRepository;

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
                request.getDoctorDetails());

        prescriptionRepository.save(prescription);

        MemberSubscription subscription = new MemberSubscription(
                subscriptionId,
                memberId,
                LocalDate.now(),
                prescriptionId,
                request.getRefillFrequency(),
                request.getMemberLocation(),
                MemberSubscription.SubscriptionStatus.ACTIVE);

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

    public List<Map<String, Object>> getRefillDues(LocalDate dueDate) {
        List<MemberSubscription> activeSubscriptions = subscriptionRepository.findBySubscriptionStatus(
                MemberSubscription.SubscriptionStatus.ACTIVE);

        return activeSubscriptions.stream()
                .filter(subscription -> isRefillDue(subscription, dueDate))
                .map(this::mapToRefillDueResponse)
                .collect(Collectors.toList());
    }

    private boolean isRefillDue(MemberSubscription subscription, LocalDate dueDate) {
        LocalDate subscriptionDate = subscription.getSubscriptionDate();
        MemberSubscription.RefillFrequency frequency = subscription.getRefillFrequency();

        switch (frequency) {
            case WEEKLY:
                return subscriptionDate.plusWeeks(1).isBefore(dueDate) ||
                        subscriptionDate.plusWeeks(1).isEqual(dueDate);
            case MONTHLY:
                return subscriptionDate.plusMonths(1).isBefore(dueDate) ||
                        subscriptionDate.plusMonths(1).isEqual(dueDate);
            default:
                return false;
        }
    }

    private Map<String, Object> mapToRefillDueResponse(MemberSubscription subscription) {
        Map<String, Object> response = new HashMap<>();
        response.put("subscriptionId", subscription.getSubscriptionId());
        response.put("memberId", subscription.getMemberId());
        response.put("memberLocation", subscription.getMemberLocation());
        response.put("dueDate", getNextDueDate(subscription));
        response.put("refillFrequency", subscription.getRefillFrequency().toString());

        MemberPrescription prescription = prescriptionRepository.findById(subscription.getPrescriptionId())
                .orElse(null);
        if (prescription != null) {
            List<Map<String, Object>> prescriptions = new ArrayList<>();
            Map<String, Object> prescriptionMap = new HashMap<>();
            prescriptionMap.put("prescriptionId", prescription.getPrescriptionId());
            prescriptionMap.put("drugId", prescription.getDrugId());
            prescriptionMap.put("dosagePerDay", prescription.getDosagePerDay());
            prescriptionMap.put("prescriptionCourse", prescription.getPrescriptionCourse());
            prescriptions.add(prescriptionMap);
            response.put("prescriptions", prescriptions);
        }

        return response;
    }

    private LocalDate getNextDueDate(MemberSubscription subscription) {
        LocalDate subscriptionDate = subscription.getSubscriptionDate();
        MemberSubscription.RefillFrequency frequency = subscription.getRefillFrequency();

        switch (frequency) {
            case WEEKLY:
                return subscriptionDate.plusWeeks(1);
            case MONTHLY:
                return subscriptionDate.plusMonths(1);
            default:
                return subscriptionDate;
        }
    }

    public boolean validateSubscription(String subscriptionId, String memberId) {
        Optional<MemberSubscription> subscription = subscriptionRepository.findById(subscriptionId);
        return subscription.isPresent() && subscription.get().getMemberId().equals(memberId);
    }
}