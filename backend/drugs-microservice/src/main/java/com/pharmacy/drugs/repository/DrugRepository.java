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
    List<Drug> findByDrugNameContainingIgnoreCase(String drugName);
    
    @Query("SELECT d FROM Drug d WHERE d.expiryDate > :currentDate")
    List<Drug> findValidDrugs(@Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT d FROM Drug d WHERE d.drugName LIKE %:drugName% AND d.expiryDate > :currentDate")
    List<Drug> findByDrugNameContainingIgnoreCaseAndValidExpiry(@Param("drugName") String drugName,
                                                                @Param("currentDate") LocalDate currentDate);
}