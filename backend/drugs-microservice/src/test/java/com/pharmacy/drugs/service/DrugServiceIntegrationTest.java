package com.pharmacy.drugs.service;

import com.pharmacy.drugs.entity.Drug;
import com.pharmacy.drugs.entity.DrugLocation;
import com.pharmacy.drugs.entity.DrugLocationId;
import com.pharmacy.drugs.repository.DrugLocationRepository;
import com.pharmacy.drugs.repository.DrugRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs against the H2 dev database seeded from data.sql. Each test uses its own
 * drug/location so tests don't depend on each other's stock changes.
 */
@SpringBootTest(properties = "jwt.secret=test-secret-0123456789-0123456789-0123456789-0123456789-0123456789")
class DrugServiceIntegrationTest {

    @Autowired
    private DrugService drugService;
    @Autowired
    private DrugRepository drugRepository;
    @Autowired
    private DrugLocationRepository drugLocationRepository;

    private int stock(String drugId, String location) {
        return drugLocationRepository.findById(new DrugLocationId(drugId, location)).orElseThrow()
                   .getQuantityAvailable();
    }

    @Test
    void validInStockDrugIsDispatchable() {
        // The original bug: this returned false for every valid drug.
        assertThat(drugService.isDispatchable("D001", "New York", 1)).isTrue();
        assertThat(drugService.isDispatchable("D001", "New York", 10_000)).isFalse();
        assertThat(drugService.isDispatchable("NO-SUCH-DRUG", "New York", 1)).isFalse();
    }

    @Test
    void nameSearchIsCaseInsensitiveAndMatchesWildcardsLiterally() {
        assertThat(drugService.searchDrugsByName("para")).extracting(Drug::getDrugId).containsExactly("D001");
        assertThat(drugService.searchDrugsByName("PARACETAMOL")).extracting(Drug::getDrugId).containsExactly("D001");
        // '%' and '_' are LIKE wildcards; user input must not act as one (was: '%' matched every drug)
        assertThat(drugService.searchDrugsByName("%")).isEmpty();
        assertThat(drugService.searchDrugsByName("_")).isEmpty();
    }

    @Test
    void drugExpiringTodayIsNotAvailableAnywhere() {
        // One rule everywhere: a drug counts as expired on its expiry date.
        drugRepository.save(new Drug("DTODAY", "Expires Today", "Test", LocalDate.now().minusYears(1),
            LocalDate.now(), 10, new BigDecimal("5.00"), "Test composition"));
        drugLocationRepository.save(new DrugLocation("DTODAY", "Chicago", 50));

        assertThat(drugService.isDispatchable("DTODAY", "Chicago", 1)).isFalse();
        var stock = drugService.getDispatchableDrugStock(
            new com.pharmacy.drugs.dto.DrugStockRequest(List.of("DTODAY"), "Chicago"));
        assertThat(stock).singleElement().satisfies(s -> assertThat(s.getIsAvailable()).isFalse());
    }

    @Test
    void reserveDecrementsStockAndReleaseRestoresIt() {
        int before = stock("D004", "New York");

        assertThat(drugService.reserveStock("D004", "New York", 5)).isTrue();
        assertThat(stock("D004", "New York")).isEqualTo(before - 5);

        assertThat(drugService.releaseStock("D004", "New York", 5)).isTrue();
        assertThat(stock("D004", "New York")).isEqualTo(before);
    }

    @Test
    void expiredStockCannotBeDispatchedOrReserved() {
        // The original bug let expired (and unknown) drugs through.
        drugRepository.save(new Drug("DEXP", "Expired Drug", "Test", LocalDate.now().minusYears(2),
            LocalDate.now().minusDays(1), 10, new BigDecimal("5.00"), "Test composition"));
        drugLocationRepository.save(new DrugLocation("DEXP", "New York", 100));

        assertThat(drugService.isDispatchable("DEXP", "New York", 1)).isFalse();
        assertThat(drugService.reserveStock("DEXP", "New York", 1)).isFalse();
        assertThat(stock("DEXP", "New York")).isEqualTo(100);
    }

    @Test
    void concurrentReservationsNeverOversell() throws Exception {
        String drug = "D003";
        String location = "Chicago";
        int available = stock(drug, location);   // 45 in the seed data
        int threads = 20;
        int perOrder = 5;                        // 20 x 5 = 100 requested, far more than available

        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            results.add(pool.submit(() -> {
                start.await();
                return drugService.reserveStock(drug, location, perOrder);
            }));
        }
        start.countDown();                       // release all threads at once
        int succeeded = 0;
        for (Future<Boolean> result : results) {
            if (result.get(30, TimeUnit.SECONDS)) {
                succeeded++;
            }
        }
        pool.shutdown();

        assertThat(succeeded).isEqualTo(available / perOrder);
        assertThat(stock(drug, location)).isEqualTo(available - succeeded * perOrder).isNotNegative();
    }
}
