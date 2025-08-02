package com.pharmacy.refill.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pharmacy.refill.dto.AdhocRefillRequest;
import com.pharmacy.refill.dto.AdhocRefillResponse;
import com.pharmacy.refill.dto.RefillDueResponse;
import com.pharmacy.refill.dto.RefillStatusResponse;
import com.pharmacy.refill.entity.RefillOrder;
import com.pharmacy.refill.security.JwtUtils;
import com.pharmacy.refill.service.RefillService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/refill")
@Tag(name = "Refill Management", description = "APIs for managing refill orders and subscriptions")
@SecurityRequirement(name = "bearerAuth")
public class RefillController {

    @Autowired
    private RefillService refillService;

    @Autowired
    private JwtUtils jwtUtils;

    @GetMapping("/viewRefillStatus")
    @Operation(summary = "Get latest refill status", description = "Retrieve the latest refill order status for the authenticated member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved refill status", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefillStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "No refill orders found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> viewRefillStatus(@RequestHeader("Authorization") String token) {
        try {
            String cleanToken = token.replace("Bearer ", "");
            String memberId = jwtUtils.getMemberIdFromJwtToken(cleanToken);

            if (memberId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token", "message", "Member ID not found in token"));
            }

            RefillStatusResponse response = refillService.getLatestRefillStatus(cleanToken, memberId);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("No refill orders found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Not Found", "message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server Error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server Error", "message", "An unexpected error occurred"));
        }
    }

    @GetMapping("/getRefillDuesAsOfDate")
    @Operation(summary = "Get refill dues as of date", description = "Retrieve all subscriptions that are due for refill as of the specified date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved refill dues", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid date format", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> getRefillDuesAsOfDate(
            @RequestHeader("Authorization") String token,
            @Parameter(description = "Due date in YYYY-MM-DD format", example = "2024-01-15") @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        try {
            String cleanToken = token.replace("Bearer ", "");

            if (!jwtUtils.validateJwtToken(cleanToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token", "message", "Token validation failed"));
            }

            List<RefillDueResponse> response = refillService.getRefillDuesAsOfDate(cleanToken, dueDate);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server Error", "message", "An unexpected error occurred"));
        }
    }

    @PostMapping("/requestAdhocRefill")
    @Operation(summary = "Request adhoc refill", description = "Create a new adhoc refill order for the specified prescriptions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created adhoc refill order", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AdhocRefillResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data or insufficient stock", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> requestAdhocRefill(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody AdhocRefillRequest request) {
        try {
            String cleanToken = token.replace("Bearer ", "");
            String tokenMemberId = jwtUtils.getMemberIdFromJwtToken(cleanToken);

            if (tokenMemberId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token", "message", "Member ID not found in token"));
            }

            // Validate that the request is for the authenticated member
            if (!tokenMemberId.equals(request.getMemberId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Access Denied", "message",
                                "Cannot create refill order for different member"));
            }

            AdhocRefillResponse response = refillService.createAdhocRefillOrder(cleanToken, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not available") || e.getMessage().contains("Failed to reserve")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Stock Unavailable", "message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server Error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server Error", "message", "An unexpected error occurred"));
        }
    }

    @GetMapping("/orders")
    @Operation(summary = "Get all refill orders", description = "Retrieve all refill orders for the authenticated member")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved refill orders", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> getAllRefillOrders(@RequestHeader("Authorization") String token) {
        try {
            String cleanToken = token.replace("Bearer ", "");
            String memberId = jwtUtils.getMemberIdFromJwtToken(cleanToken);

            if (memberId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token", "message", "Member ID not found in token"));
            }

            List<RefillStatusResponse> response = refillService.getAllRefillOrdersForMember(memberId);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server Error", "message", "An unexpected error occurred"));
        }
    }

    @PutMapping("/orders/{refillOrderId}/status")
    @Operation(summary = "Update refill order status", description = "Update the status of a refill order (Admin operation)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated order status", content = @Content(mediaType = "application/json", schema = @Schema(implementation = RefillStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "Refill order not found", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized access", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<?> updateRefillOrderStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable String refillOrderId,
            @RequestParam RefillOrder.OrderStatus status) {
        try {
            String cleanToken = token.replace("Bearer ", "");

            if (!jwtUtils.validateJwtToken(cleanToken)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token", "message", "Token validation failed"));
            }

            RefillStatusResponse response = refillService.updateRefillOrderStatus(refillOrderId, status);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Not Found", "message", e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server Error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Server Error", "message", "An unexpected error occurred"));
        }
    }
}