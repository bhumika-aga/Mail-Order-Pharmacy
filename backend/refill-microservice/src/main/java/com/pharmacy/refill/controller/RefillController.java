package com.pharmacy.refill.controller;

import com.pharmacy.refill.dto.AdhocRefillRequest;
import com.pharmacy.refill.dto.AdhocRefillResponse;
import com.pharmacy.refill.dto.RefillDueResponse;
import com.pharmacy.refill.dto.RefillStatusResponse;
import com.pharmacy.refill.entity.RefillOrder;
import com.pharmacy.refill.exception.ApiException;
import com.pharmacy.refill.service.RefillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Every endpoint here is reached only with a JWT the security filter has already
 * validated; the filter also exposes the token's member ID as a request
 * attribute. Errors are raised as {@link ApiException} and rendered by
 * {@code ApiExceptionHandler}.
 */
@RestController
@RequestMapping("/api/refill")
@Tag(name = "Refill Management", description = "APIs for managing refill orders and subscriptions")
@SecurityRequirement(name = "bearerAuth")
public class RefillController {
    
    private final RefillService refillService;
    
    public RefillController(RefillService refillService) {
        this.refillService = refillService;
    }
    
    /**
     * Member ID from the validated token (null for admin accounts).
     */
    private static String memberId(HttpServletRequest request) {
        return (String) request.getAttribute("memberId");
    }
    
    /**
     * The caller's validated bearer token, forwarded to other services.
     */
    private static String bearerToken(HttpServletRequest request) {
        return request.getHeader(HttpHeaders.AUTHORIZATION).substring("Bearer ".length());
    }
    
    @GetMapping("/viewRefillStatus")
    @Operation(summary = "Get latest refill status", description = "Retrieve the latest refill order status for the authenticated member")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved refill status", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefillStatusResponse.class))),
        @ApiResponse(responseCode = "404", description = "No refill orders found", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<RefillStatusResponse> viewRefillStatus(HttpServletRequest request) {
        String memberId = memberId(request);
        // An account without a member ID (an admin) has no refill orders of its own.
        if (memberId == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Not Found", "No refill orders found for this account");
        }
        return ResponseEntity.ok(refillService.getLatestRefillStatus(memberId));
    }
    
    @GetMapping("/getRefillDuesAsOfDate")
    @Operation(summary = "Get refill dues as of date",
        description = "Subscriptions whose next scheduled refill falls on or before the date (the member's own; admins see all)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved refill dues", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid date format", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "503", description = "Subscription service unavailable", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<RefillDueResponse>> getRefillDuesAsOfDate(
        HttpServletRequest request,
        @Parameter(description = "Due date in YYYY-MM-DD format", example = "2024-01-15") @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        return ResponseEntity.ok(refillService.getRefillDuesAsOfDate(bearerToken(request), dueDate));
    }
    
    @PostMapping("/requestAdhocRefill")
    @Operation(summary = "Request adhoc refill", description = "Create a new adhoc refill order for the specified prescriptions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Successfully created adhoc refill order", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdhocRefillResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid prescription, unknown drug or insufficient stock", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "403", description = "Order is for a different member, or caller is not a member", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "503", description = "Drugs or subscription service unavailable", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<AdhocRefillResponse> requestAdhocRefill(
        HttpServletRequest httpRequest,
        @Valid @RequestBody AdhocRefillRequest request) {
        String memberId = memberId(httpRequest);
        if (memberId == null) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied", "Only member accounts can request refills");
        }
        if (!memberId.equals(request.getMemberId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access Denied",
                "Cannot create refill order for different member");
        }
        AdhocRefillResponse response = refillService.createAdhocRefillOrder(bearerToken(httpRequest), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/orders")
    @Operation(summary = "Get all refill orders", description = "Retrieve all refill orders for the authenticated member")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved refill orders", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<List<RefillStatusResponse>> getAllRefillOrders(HttpServletRequest request) {
        String memberId = memberId(request);
        // Admin accounts have no member ID and therefore no orders of their own.
        return ResponseEntity.ok(memberId == null ? List.of() : refillService.getAllRefillOrdersForMember(memberId));
    }
    
    @PutMapping("/orders/{refillOrderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update refill order status", description = "Update the status of a refill order (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated order status", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefillStatusResponse.class))),
        @ApiResponse(responseCode = "404", description = "Refill order not found", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "403", description = "Caller is not an admin", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<RefillStatusResponse> updateRefillOrderStatus(
        @PathVariable String refillOrderId,
        @RequestParam RefillOrder.OrderStatus status) {
        return ResponseEntity.ok(refillService.updateRefillOrderStatus(refillOrderId, status));
    }
}
