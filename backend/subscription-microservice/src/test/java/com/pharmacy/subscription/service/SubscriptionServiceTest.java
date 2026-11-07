package com.pharmacy.subscription.service;

import com.pharmacy.subscription.client.DrugServiceClient;
import com.pharmacy.subscription.entity.MemberSubscription;
import com.pharmacy.subscription.entity.MemberSubscription.RefillFrequency;
import com.pharmacy.subscription.repository.MemberPrescriptionRepository;
import com.pharmacy.subscription.repository.MemberSubscriptionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.pharmacy.subscription.entity.MemberSubscription.SubscriptionStatus.ACTIVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SubscriptionServiceTest {

    private static LocalDate date(String iso) {
        return LocalDate.parse(iso);
    }

    // --- nextRefillDate: the scheduling rule ---------------------------------

    @Test
    void oldMonthlySubscriptionGetsACurrentDueDateNotItsFirstOne() {
        // The original bug: a 2024 subscription always reported 2024-02-15 as its due date.
        assertThat(SubscriptionService.nextRefillDate(date("2024-01-15"), RefillFrequency.MONTHLY, date("2026-11-07")))
            .isEqualTo(date("2026-11-15"));
    }

    @Test
    void refillScheduledForTodayIsDueToday() {
        assertThat(SubscriptionService.nextRefillDate(date("2024-01-15"), RefillFrequency.MONTHLY, date("2026-11-15")))
            .isEqualTo(date("2026-11-15"));
    }

    @Test
    void weeklyScheduleRollsForwardToTheNextWeek() {
        // 2024-01-20 is a Saturday; the next Saturday on/after 2026-11-07 (itself a Saturday) is that day
        assertThat(SubscriptionService.nextRefillDate(date("2024-01-20"), RefillFrequency.WEEKLY, date("2026-11-07")))
            .isEqualTo(date("2026-11-07"));
        assertThat(SubscriptionService.nextRefillDate(date("2024-01-20"), RefillFrequency.WEEKLY, date("2026-11-08")))
            .isEqualTo(date("2026-11-14"));
    }

    @Test
    void subscriptionDateItselfIsNotARefill() {
        // Subscribed today: the first refill is one period later.
        assertThat(SubscriptionService.nextRefillDate(date("2026-11-07"), RefillFrequency.MONTHLY, date("2026-11-07")))
            .isEqualTo(date("2026-12-07"));
        assertThat(SubscriptionService.nextRefillDate(date("2026-11-07"), RefillFrequency.WEEKLY, date("2026-11-07")))
            .isEqualTo(date("2026-11-14"));
    }

    @Test
    void monthEndDatesDoNotDrift() {
        // Jan 31 -> Feb 28 (short month) -> back to Mar 31, not Mar 28
        assertThat(SubscriptionService.nextRefillDate(date("2026-01-31"), RefillFrequency.MONTHLY, date("2026-02-01")))
            .isEqualTo(date("2026-02-28"));
        assertThat(SubscriptionService.nextRefillDate(date("2026-01-31"), RefillFrequency.MONTHLY, date("2026-03-01")))
            .isEqualTo(date("2026-03-31"));
    }

    // --- getRefillDues: "as of" is a look-ahead from today --------------------

    @Test
    void refillDuesIncludeOnlySubscriptionsDueByTheAsOfDate() {
        MemberSubscriptionRepository subscriptions = mock(MemberSubscriptionRepository.class);
        MemberPrescriptionRepository prescriptions = mock(MemberPrescriptionRepository.class);
        when(prescriptions.findById(any())).thenReturn(Optional.empty());
        // next refills (today = 2026-11-07): SUB-A on 2026-11-15, SUB-B on 2026-11-07 (today)
        when(subscriptions.findByMemberIdAndSubscriptionStatus("MEM001", ACTIVE)).thenReturn(List.of(
            new MemberSubscription("SUB-A", "MEM001", date("2024-01-15"), "P1", RefillFrequency.MONTHLY, "New York", ACTIVE),
            new MemberSubscription("SUB-B", "MEM001", date("2026-10-07"), "P2", RefillFrequency.MONTHLY, "New York", ACTIVE)));

        Clock today = Clock.fixed(date("2026-11-07").atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        SubscriptionService service = new SubscriptionService(prescriptions, subscriptions,
            mock(DrugServiceClient.class), mock(PlatformTransactionManager.class), today);

        List<Map<String, Object>> dueToday = service.getRefillDues(date("2026-11-07"), "MEM001");
        assertThat(dueToday).extracting(d -> d.get("subscriptionId")).containsExactly("SUB-B");

        List<Map<String, Object>> dueWithinTenDays = service.getRefillDues(date("2026-11-17"), "MEM001");
        assertThat(dueWithinTenDays).extracting(d -> d.get("subscriptionId")).containsExactly("SUB-B", "SUB-A");
        assertThat(dueWithinTenDays.get(1).get("dueDate")).isEqualTo(date("2026-11-15"));
    }
}
