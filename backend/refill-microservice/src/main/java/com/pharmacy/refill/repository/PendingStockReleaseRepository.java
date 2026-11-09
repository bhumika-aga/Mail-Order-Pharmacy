package com.pharmacy.refill.repository;

import com.pharmacy.refill.entity.PendingStockRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PendingStockReleaseRepository extends JpaRepository<PendingStockRelease, String> {
    
    /**
     * The oldest pending releases that are due for another attempt, a batch at a time.
     */
    List<PendingStockRelease> findTop100ByNextAttemptAtLessThanEqualOrderByCreatedAtAsc(LocalDateTime now);
}
