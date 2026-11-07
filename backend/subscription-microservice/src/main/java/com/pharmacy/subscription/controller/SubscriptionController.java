package com.pharmacy.subscription.controller;

import com.pharmacy.subscription.dto.SubscriptionRequest;
import com.pharmacy.subscription.entity.MemberPrescription;
import com.pharmacy.subscription.entity.MemberSubscription;
import com.pharmacy.subscription.exception.ApiException;
import com.pharmacy.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@Tag(name = "Subscriptions", description = "Mail-order pharmacy subscription management APIs")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {
    
    private final SubscriptionService subscriptionService;
    
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
    
    private static boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                                             .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
    
    // Errors are raised as ApiException and rendered by ApiExceptionHandler.

    @PostMapping("/subscribe")
    @Operation(summary = "Create new subscription", description = "Create a new mail-order pharmacy subscription")
    public ResponseEntity<MemberSubscription> subscribe(@Valid @RequestBody SubscriptionRequest request,
                                                        HttpServletRequest httpRequest) {
        String memberId = requireMemberId(httpRequest);
        // The security filter has already validated this bearer token.
        String token = httpRequest.getHeader(HttpHeaders.AUTHORIZATION).substring("Bearer ".length());
        return ResponseEntity.ok(subscriptionService.subscribe(request, memberId, token));
    }

    @PostMapping("/unsubscribe")
    @Operation(summary = "Cancel subscription", description = "Cancel one of the authenticated member's subscriptions")
    public ResponseEntity<Map<String, String>> unsubscribe(
        @Parameter(description = "Subscription ID to cancel", required = true) @RequestParam String subscriptionId,
        HttpServletRequest httpRequest) {
        subscriptionService.unsubscribe(subscriptionId, requireMemberId(httpRequest));
        return ResponseEntity.ok(Map.of("message", "Subscription cancelled successfully"));
    }

    /** Admin accounts have no member ID, so they cannot hold subscriptions. */
    private static String requireMemberId(HttpServletRequest request) {
        String memberId = (String) request.getAttribute("memberId");
        if (memberId == null) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Only member accounts can manage subscriptions");
        }
        return memberId;
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
    @Operation(summary = "Get subscription details",
        description = "Get details of one of the authenticated member's subscriptions (admins can read any)")
    public ResponseEntity<MemberSubscription> getSubscriptionById(
        @Parameter(description = "Subscription ID", required = true) @PathVariable String subscriptionId,
        HttpServletRequest request, Authentication authentication) {
        String memberId = (String) request.getAttribute("memberId");
        boolean admin = isAdmin(authentication);
        // Another member's subscription is reported as not found, so IDs can't be probed.
        return subscriptionService.getSubscriptionById(subscriptionId)
                   .filter(subscription -> admin || subscription.getMemberId().equals(memberId))
                   .map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/refillDues")
    @Operation(summary = "Get refill dues",
        description = "Get the authenticated member's subscriptions that are due for refill as of the specified date (admins see all members)")
    public ResponseEntity<List<Map<String, Object>>> getRefillDues(
        @Parameter(description = "Due date in YYYY-MM-DD format") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
        HttpServletRequest request, Authentication authentication) {
        String memberFilter = isAdmin(authentication) ? null : (String) request.getAttribute("memberId");
        if (memberFilter == null && !isAdmin(authentication)) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(subscriptionService.getRefillDues(dueDate, memberFilter));
    }
}