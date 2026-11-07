package com.pharmacy.drugs.repository;

import com.pharmacy.drugs.entity.Drug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DrugRepository extends JpaRepository<Drug, String> {
    
    @Query("SELECT d FROM Drug d WHERE d.expiryDate > :currentDate")
    List<Drug> findValidDrugs(@Param("currentDate") LocalDate currentDate);
    
    /**
     * Case-insensitive "contains" search over unexpired drugs. {@code pattern} must
     * already be a LIKE pattern with {@code %}, {@code _} and {@code \} escaped by
     * the caller (see DrugService), so user input is matched literally.
     */
    @Query("SELECT d FROM Drug d WHERE LOWER(d.drugName) LIKE LOWER(:pattern) ESCAPE '\\' "
               + "AND d.expiryDate > :currentDate ORDER BY d.drugName")
    List<Drug> searchValidDrugsByName(@Param("pattern") String pattern,
                                      @Param("currentDate") LocalDate currentDate);
}