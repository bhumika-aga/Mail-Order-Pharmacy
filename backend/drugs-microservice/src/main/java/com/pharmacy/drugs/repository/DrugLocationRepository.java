package com.pharmacy.drugs.repository;

import com.pharmacy.drugs.entity.DrugLocation;
import com.pharmacy.drugs.entity.DrugLocationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DrugLocationRepository extends JpaRepository<DrugLocation, DrugLocationId> {
    
    List<DrugLocation> findByDrugId(String drugId);
    
    @Query("SELECT dl FROM DrugLocation dl WHERE dl.drugId IN :drugIds AND dl.location = :location")
    List<DrugLocation> findByDrugIdsAndLocation(@Param("drugIds") List<String> drugIds,
                                                @Param("location") String location);
    
    /**
     * Decrements stock in a single conditional UPDATE, so the database checks and
     * changes the quantity atomically: concurrent reservations can never oversell.
     *
     * @return 1 if the stock was reserved, 0 if the row is missing or has too little stock
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE DrugLocation dl SET dl.quantityAvailable = dl.quantityAvailable - :quantity "
               + "WHERE dl.drugId = :drugId AND dl.location = :location AND dl.quantityAvailable >= :quantity")
    int decrementStock(@Param("drugId") String drugId, @Param("location") String location,
                       @Param("quantity") int quantity);
    
    /**
     * Returns previously reserved stock (compensation when an order fails).
     *
     * @return 1 if the stock row exists and was incremented, otherwise 0
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE DrugLocation dl SET dl.quantityAvailable = dl.quantityAvailable + :quantity "
               + "WHERE dl.drugId = :drugId AND dl.location = :location")
    int incrementStock(@Param("drugId") String drugId, @Param("location") String location,
                       @Param("quantity") int quantity);
    
    @Query(value = "SELECT COUNT(*) FROM stock_releases WHERE release_id = :releaseId", nativeQuery = true)
    long countAppliedReleases(@Param("releaseId") String releaseId);
    
    /**
     * Records a release as applied. The primary key rejects a second record of the
     * same release ID, which rolls back that (duplicate) release.
     */
    @Modifying(flushAutomatically = true)
    @Query(value = "INSERT INTO stock_releases (release_id, drug_id, location, quantity, released_at) "
                       + "VALUES (:releaseId, :drugId, :location, :quantity, CURRENT_TIMESTAMP)", nativeQuery = true)
    void recordAppliedRelease(@Param("releaseId") String releaseId, @Param("drugId") String drugId,
                              @Param("location") String location, @Param("quantity") int quantity);
    
    /**
     * Forgets applied releases older than {@code cutoff}; callers only retry a
     * release for a short while, so old records can no longer prevent a duplicate.
     */
    @Modifying
    @Query(value = "DELETE FROM stock_releases WHERE released_at < :cutoff", nativeQuery = true)
    int deleteAppliedReleasesBefore(@Param("cutoff") LocalDateTime cutoff);
}
