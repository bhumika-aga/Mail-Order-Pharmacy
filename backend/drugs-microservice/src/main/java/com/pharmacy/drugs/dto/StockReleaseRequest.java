package com.pharmacy.drugs.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request payload for returning (incrementing) reserved drug stock at a location.
 *
 * @param releaseId the caller's ID for this release: a release with an ID that was
 *                  already applied is not applied again, so callers can safely retry.
 *                  Optional; without one, every call is applied.
 */
public record StockReleaseRequest(
    @NotBlank(message = "Drug code is required") String drugCode,
    @NotBlank(message = "Location is required") String location,
    @NotNull(message = "Quantity is required") @Min(value = 1, message = "Quantity must be at least 1") Integer quantity,
    @Size(max = 64, message = "Release ID must not exceed 64 characters") String releaseId) {
}
