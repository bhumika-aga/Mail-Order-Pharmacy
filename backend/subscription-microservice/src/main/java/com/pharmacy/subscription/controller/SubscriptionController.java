package com.pharmacy.subscription.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.subscription.dto.SubscriptionRequest;
import com.pharmacy.subscription.entity.MemberPrescription;
import com.pharmacy.subscription.entity.MemberSubscription;
import com.pharmacy.subscription.service.SubscriptionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscriptions", description = "Mail-order pharmacy subscription management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PostMapping("/subscribe")
    @Operation(summary = "Create new subscription", description = "Create a new mail-order pharmacy subscription")
    public ResponseEntity<?> subscribe(@Valid @RequestBody SubscriptionRequest request,
            HttpServletRequest httpRequest) {
        try {
            String memberId = (String) httpRequest.getAttribute("memberId");
            String token = httpRequest.getHeader("Authorization").replace("Bearer ", "");

            MemberSubscription subscription = subscriptionService.subscribe(request, memberId, token);
            return ResponseEntity.ok(subscription);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "Cancel subscription", description = "Cancel an existing subscription")
    public ResponseEntity<?> unsubscribe(
            @Parameter(description = "Subscription ID to cancel", required = true) @RequestParam String subscriptionId,
            HttpServletRequest httpRequest) {
        try {
            String memberId = (String) httpRequest.getAttribute("memberId");

            boolean result = subscriptionService.unsubscribe(subscriptionId, memberId);

            if (result) {
                return ResponseEntity.ok(Map.of("message", "Subscription cancelled successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/my-subscriptions")
    @Operation(summary = "Get member subscriptions", description = "Get all subscriptions for the authenticated member")
    public ResponseEntity<List<MemberSubscription>> getMemberSubscriptions(HttpServletRequest request) {
        String memberId = (String) request.getAttribute("memberId");
        List<MemberSubscription> subscriptions = subscriptionService.getMemberSubscriptions(memberId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/active")
    @Operation(summary = "Get active subscriptions", description = "Get all active subscriptions for the authenticated member")
    public ResponseEntity<List<MemberSubscription>> getActiveSubscriptions(HttpServletRequest request) {
        String memberId = (String) request.getAttribute("memberId");
        List<MemberSubscription> subscriptions = subscriptionService.getActiveSubscriptions(memberId);
        return ResponseEntity.ok(subscriptions);
    }

    @GetMapping("/prescriptions")
    @Operation(summary = "Get member prescriptions", description = "Get all prescriptions for the authenticated member")
    public ResponseEntity<List<MemberPrescription>> getMemberPrescriptions(HttpServletRequest request) {
        String memberId = (String) request.getAttribute("memberId");
        List<MemberPrescription> prescriptions = subscriptionService.getMemberPrescriptions(memberId);
        return ResponseEntity.ok(prescriptions);
    }

    @GetMapping("/{subscriptionId}")
    @Operation(summary = "Get subscription details", description = "Get details of a specific subscription")
    public ResponseEntity<MemberSubscription> getSubscriptionById(
            @Parameter(description = "Subscription ID", required = true) @PathVariable String subscriptionId) {
        return subscriptionService.getSubscriptionById(subscriptionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/refillDues")
    @Operation(summary = "Get refill dues", description = "Get subscriptions that are due for refill as of the specified date")
    public ResponseEntity<List<Map<String, Object>>> getRefillDues(
            @Parameter(description = "Due date in YYYY-MM-DD format") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        List<Map<String, Object>> refillDues = subscriptionService.getRefillDues(dueDate);
        return ResponseEntity.ok(refillDues);
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate subscription", description = "Validate subscription ownership for a member")
    public ResponseEntity<Boolean> validateSubscription(
            @Parameter(description = "Subscription ID") @RequestParam String subscriptionId,
            @Parameter(description = "Member ID") @RequestParam String memberId) {
        boolean isValid = subscriptionService.validateSubscription(subscriptionId, memberId);
        return ResponseEntity.ok(isValid);
    }
}