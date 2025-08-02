package com.pharmacy.drugs.repository;

import com.pharmacy.drugs.entity.DrugLocation;
import com.pharmacy.drugs.entity.DrugLocationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrugLocationRepository extends JpaRepository<DrugLocation, DrugLocationId> {
    List<DrugLocation> findByLocation(String location);
    
    List<DrugLocation> findByDrugId(String drugId);
    
    @Query("SELECT dl FROM DrugLocation dl WHERE dl.drugId IN :drugIds AND dl.location = :location")
    List<DrugLocation> findByDrugIdsAndLocation(@Param("drugIds") List<String> drugIds,
                                                @Param("location") String location);
    
    @Query("SELECT dl FROM DrugLocation dl WHERE dl.location = :location AND dl.quantityAvailable > 0")
    List<DrugLocation> findAvailableDrugsByLocation(@Param("location") String location);
}