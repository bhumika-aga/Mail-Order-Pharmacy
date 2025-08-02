package com.pharmacy.drugs.entity;

import java.io.Serializable;
import java.util.Objects;

public class DrugLocationId implements Serializable {
    private String drugId;
    private String location;

    public DrugLocationId() {
    }

    public DrugLocationId(String drugId, String location) {
        this.drugId = drugId;
        this.location = location;
    }

    public String getDrugId() {
        return drugId;
    }

    public void setDrugId(String drugId) {
        this.drugId = drugId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        DrugLocationId that = (DrugLocationId) o;
        return Objects.equals(drugId, that.drugId) && Objects.equals(location, that.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(drugId, location);
    }
}