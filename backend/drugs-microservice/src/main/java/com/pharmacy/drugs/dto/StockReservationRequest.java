package com.pharmacy.drugs.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for reserving (decrementing) drug stock at a location.
 */
public record StockReservationRequest(
    @NotBlank(message = "Drug code is required") String drugCode,
    @NotBlank(message = "Location is required") String location,
    @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity) {
}
