package com.pharmacy.refill.service;

import com.pharmacy.refill.client.DrugServiceClient;
import com.pharmacy.refill.entity.PendingStockRelease;
import com.pharmacy.refill.repository.PendingStockReleaseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Returns reserved stock to the drugs service, reliably.
 * <ol>
 *   <li>{@link #queue} stores the releases, inside the transaction that frees the
 *       stock (an order cancelled or deleted), so they can't be lost.</li>
 *   <li>{@link #send} then tries them straight away; each one the drugs service
 *       confirms is removed.</li>
 *   <li>Any that fail (drugs service down) stay stored and are retried by
 *       {@link #retryPending} every minute until they succeed.</li>
 * </ol>
 * Each release carries its own ID and the drugs service ignores one it has already
 * applied, so a retry can never return the same stock twice, even when the drugs
 * service applied a release but its reply was lost.
 */
@Service
public class StockReleaseService {
    
    private static final Logger log = LoggerFactory.getLogger(StockReleaseService.class);
    
    private final PendingStockReleaseRepository repository;
    private final DrugServiceClient drugServiceClient;
    
    public StockReleaseService(PendingStockReleaseRepository repository, DrugServiceClient drugServiceClient) {
        this.repository = repository;
        this.drugServiceClient = drugServiceClient;
    }
    
    /**
     * Stores releases for sending; must run inside the transaction that frees the stock.
     */
    public List<PendingStockRelease> queue(List<PendingStockRelease> releases) {
        return repository.saveAll(releases);
    }
    
    /**
     * Sends releases to the drugs service now. Those that fail are stored (if they
     * weren't already) and retried later. Never throws, so a failed release can't
     * hide the outcome of the operation that triggered it.
     */
    public void send(List<PendingStockRelease> releases) {
        for (PendingStockRelease release : releases) {
            send(release);
        }
    }
    
    @Scheduled(fixedDelayString = "${stock-release.retry-delay:PT1M}",
        initialDelayString = "${stock-release.retry-delay:PT1M}")
    public void retryPending() {
        List<PendingStockRelease> pending = repository.findTop100ByOrderByCreatedAtAsc();
        if (!pending.isEmpty()) {
            log.info("Retrying {} pending stock release(s)", pending.size());
            send(pending);
        }
    }
    
    private void send(PendingStockRelease release) {
        boolean released;
        try {
            released = drugServiceClient.releaseDrugStock(release.getReleaseId(), release.getDrugCode(),
                release.getLocation(), release.getQuantity());
        } catch (RuntimeException e) {
            release.recordFailure(e.getMessage());
            log.warn("Stock release {} for order {} ({} x{} at {}) failed on attempt {}; will retry: {}",
                release.getReleaseId(), release.getRefillOrderId(), release.getDrugCode(), release.getQuantity(),
                release.getLocation(), release.getAttempts(), e.getMessage());
            try {
                repository.save(release);
            } catch (RuntimeException dbError) {
                log.error("Stock release {} for order {} ({} x{} at {}) could not be stored for retry; "
                              + "manual correction needed", release.getReleaseId(), release.getRefillOrderId(),
                    release.getDrugCode(), release.getQuantity(), release.getLocation(), dbError);
            }
            return;
        }
        if (!released) {
            // The drugs service has no stock row for this drug and location; retrying won't change that.
            log.error("Stock release {} for order {} refused ({} x{} at {}); manual correction needed",
                release.getReleaseId(), release.getRefillOrderId(), release.getDrugCode(), release.getQuantity(),
                release.getLocation());
        }
        try {
            repository.deleteById(release.getReleaseId());
        } catch (RuntimeException e) {
            // Harmless: the next retry re-sends it and the drugs service ignores the repeat.
            log.warn("Sent stock release {} could not be removed from the queue: {}",
                release.getReleaseId(), e.getMessage());
        }
    }
}
