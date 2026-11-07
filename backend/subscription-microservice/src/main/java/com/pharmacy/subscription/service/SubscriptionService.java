package com.pharmacy.subscription.service;

import com.pharmacy.subscription.client.DrugServiceClient;
import com.pharmacy.subscription.dto.SubscriptionRequest;
import com.pharmacy.subscription.entity.MemberPrescription;
import com.pharmacy.subscription.entity.MemberSubscription;
import com.pharmacy.subscription.exception.ApiException;
import com.pharmacy.subscription.repository.MemberPrescriptionRepository;
import com.pharmacy.subscription.repository.MemberSubscriptionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private final MemberPrescriptionRepository prescriptionRepository;
    private final MemberSubscriptionRepository subscriptionRepository;
    private final DrugServiceClient drugServiceClient;
    private final TransactionTemplate transactionTemplate;
    private final Clock clock;

    public SubscriptionService(MemberPrescriptionRepository prescriptionRepository,
                               MemberSubscriptionRepository subscriptionRepository,
                               DrugServiceClient drugServiceClient,
                               PlatformTransactionManager transactionManager,
                               Clock clock) {
        this.prescriptionRepository = prescriptionRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.drugServiceClient = drugServiceClient;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.clock = clock;
    }

    /**
     * Creates a prescription and an active subscription for it. The drug-service
     * call happens first, outside any transaction, so a slow drugs service never
     * holds one of the (few) pooled database connections.
     */
    public MemberSubscription subscribe(SubscriptionRequest request, String memberId, String token) {
        if (!drugServiceClient.verifyDrugAvailability(request.getDrugId(), request.getMemberLocation(), token)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Drug not available at specified location");
        }

        // Only the inserts share a transaction: both are saved or neither is.
        return transactionTemplate.execute(status -> {
            // IDs are short random strings; save() on an existing ID would silently
            // overwrite that record, so a (rare) collision is retried instead.
            String prescriptionId = newId("PRESC_", prescriptionRepository::existsById);
            String subscriptionId = newId("SUB_", subscriptionRepository::existsById);

            prescriptionRepository.save(new MemberPrescription(
                prescriptionId,
                memberId,
                request.getInsurancePolicyNumber(),
                request.getInsuranceProvider(),
                request.getPrescriptionDate(),
                request.getDrugId(),
                request.getDosagePerDay(),
                request.getPrescriptionCourse(),
                request.getDoctorDetails()));

            return subscriptionRepository.save(new MemberSubscription(
                subscriptionId,
                memberId,
                LocalDate.now(clock),
                prescriptionId,
                request.getRefillFrequency(),
                request.getMemberLocation(),
                MemberSubscription.SubscriptionStatus.ACTIVE));
        });
    }

    private static String newId(String prefix, java.util.function.Predicate<String> exists) {
        String id;
        do {
            id = prefix + UUID.randomUUID().toString().substring(0, 8);
        } while (exists.test(id));
        return id;
    }

    @Transactional
    public void unsubscribe(String subscriptionId, String memberId) {
        // Another member's subscription is reported as not found, so IDs can't be probed.
        MemberSubscription subscription = subscriptionRepository.findById(subscriptionId)
                                              .filter(s -> s.getMemberId().equals(memberId))
                                              .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                                                  "Subscription not found"));

        if (subscription.getSubscriptionStatus() == MemberSubscription.SubscriptionStatus.INACTIVE) {
            throw new ApiException(HttpStatus.CONFLICT, "Subscription is already inactive");
        }

        subscription.setSubscriptionStatus(MemberSubscription.SubscriptionStatus.INACTIVE);
        subscriptionRepository.save(subscription);
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

    /**
     * Active subscriptions whose next scheduled refill falls on or before
     * {@code asOfDate}. A null {@code memberId} returns dues for every member
     * (admin view); otherwise only that member's subscriptions.
     * <p>
     * Refills are scheduled every period (week/month) from the subscription date.
     * The "next" refill is the first scheduled date on or after today, so the
     * date acts as a look-ahead: today's date returns refills due today, next
     * Friday returns everything due by then.
     */
    public List<Map<String, Object>> getRefillDues(LocalDate asOfDate, String memberId) {
        List<MemberSubscription> activeSubscriptions = memberId == null
                                                           ? subscriptionRepository.findBySubscriptionStatus(
            MemberSubscription.SubscriptionStatus.ACTIVE)
                                                           : subscriptionRepository.findByMemberIdAndSubscriptionStatus(
            memberId, MemberSubscription.SubscriptionStatus.ACTIVE);

        LocalDate today = LocalDate.now(clock);
        List<Map<String, Object>> dues = new ArrayList<>();
        for (MemberSubscription subscription : activeSubscriptions) {
            LocalDate nextRefill = nextRefillDate(
                subscription.getSubscriptionDate(), subscription.getRefillFrequency(), today);
            if (!nextRefill.isAfter(asOfDate)) {
                dues.add(mapToRefillDueResponse(subscription, nextRefill));
            }
        }
        dues.sort(Comparator.comparing(due -> (LocalDate) due.get("dueDate")));
        return dues;
    }

    /**
     * The first scheduled refill on or after {@code today}. Refills fall on
     * {@code start + n periods} for n >= 1 (the subscription date itself is the
     * initial fill). Each date is computed from {@code start} rather than by
     * repeated addition, so month-end dates don't drift (Jan 31 -> Feb 28 -> Mar 31).
     */
    static LocalDate nextRefillDate(LocalDate start, MemberSubscription.RefillFrequency frequency, LocalDate today) {
        long periods = switch (frequency) {
            case WEEKLY -> ChronoUnit.WEEKS.between(start, today);
            case MONTHLY -> ChronoUnit.MONTHS.between(start, today);
        };
        periods = Math.max(1, periods);
        LocalDate refill = addPeriods(start, frequency, periods);
        while (refill.isBefore(today)) {
            refill = addPeriods(start, frequency, ++periods);
        }
        return refill;
    }

    private static LocalDate addPeriods(LocalDate start, MemberSubscription.RefillFrequency frequency, long periods) {
        return switch (frequency) {
            case WEEKLY -> start.plusWeeks(periods);
            case MONTHLY -> start.plusMonths(periods);
        };
    }

    private Map<String, Object> mapToRefillDueResponse(MemberSubscription subscription, LocalDate dueDate) {
        Map<String, Object> response = new HashMap<>();
        response.put("subscriptionId", subscription.getSubscriptionId());
        response.put("memberId", subscription.getMemberId());
        response.put("memberLocation", subscription.getMemberLocation());
        response.put("dueDate", dueDate);
        response.put("refillFrequency", subscription.getRefillFrequency().toString());

        prescriptionRepository.findById(subscription.getPrescriptionId()).ifPresent(prescription -> {
            Map<String, Object> prescriptionMap = new HashMap<>();
            prescriptionMap.put("prescriptionId", prescription.getPrescriptionId());
            prescriptionMap.put("drugId", prescription.getDrugId());
            prescriptionMap.put("dosagePerDay", prescription.getDosagePerDay());
            prescriptionMap.put("prescriptionCourse", prescription.getPrescriptionCourse());
            response.put("prescriptions", List.of(prescriptionMap));
        });

        return response;
    }
}
