package com.pharmacy.refill.repository;

import com.pharmacy.refill.entity.PendingStockRelease;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PendingStockReleaseRepository extends JpaRepository<PendingStockRelease, String> {
    
    /**
     * The oldest pending releases, a batch at a time.
     */
    List<PendingStockRelease> findTop100ByOrderByCreatedAtAsc();
}
