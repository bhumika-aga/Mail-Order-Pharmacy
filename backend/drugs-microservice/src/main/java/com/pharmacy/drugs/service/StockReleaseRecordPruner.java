package com.pharmacy.drugs.service;

import com.pharmacy.drugs.repository.DrugLocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Keeps the record of applied stock releases (used to ignore a repeated release)
 * from growing forever. The refill service retries an unconfirmed release at
 * least hourly, so a record only needs to outlive that by a wide margin.
 */
@Component
public class StockReleaseRecordPruner {
    
    private static final Logger log = LoggerFactory.getLogger(StockReleaseRecordPruner.class);
    
    private final DrugLocationRepository repository;
    private final TransactionTemplate transactionTemplate;
    private final Duration retention;
    
    public StockReleaseRecordPruner(DrugLocationRepository repository, PlatformTransactionManager transactionManager,
                                    @Value("${stock-release.record-retention:P30D}") Duration retention) {
        this.repository = repository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.retention = retention;
    }
    
    // Shortly after start-up, then daily (services on sleeping hosts rarely run a full day).
    @Scheduled(initialDelayString = "PT1M", fixedDelayString = "P1D")
    public void prune() {
        pruneOlderThan(retention);
    }
    
    /**
     * @return the number of records removed
     */
    public int pruneOlderThan(Duration age) {
        Integer deleted = transactionTemplate.execute(
            tx -> repository.deleteAppliedReleasesBefore(LocalDateTime.now().minus(age)));
        if (deleted != null && deleted > 0) {
            log.info("Pruned {} stock release record(s) older than {}", deleted, age);
        }
        return deleted == null ? 0 : deleted;
    }
}
