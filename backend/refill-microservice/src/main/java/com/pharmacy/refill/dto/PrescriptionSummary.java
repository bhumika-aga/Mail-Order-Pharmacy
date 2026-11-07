package com.pharmacy.refill.dto;

/**
 * The fields of a member prescription (owned by the subscription service) that
 * the refill service needs to check an adhoc refill request.
 */
public record PrescriptionSummary(String prescriptionId, String memberId, String drugId) {
}
