package com.example.bankingprojectfinal.Controller;

import com.example.bankingprojectfinal.DTOS.Transaction.TransactionDto;
import com.example.bankingprojectfinal.DTOS.Transaction.TransferRequest;
import com.example.bankingprojectfinal.Service.Abstraction.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Transaction Management", description = "APIs for initiating and viewing bank transactions")
public class TransactionController {

    private final TransactionService transactionService;

    // ==================== CUSTOMER ENDPOINTS ====================

    @Operation(summary = "Initiate card-to-card transfer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction initiated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or business rule violation"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "403", description = "Cannot transfer from another's card"),
            @ApiResponse(responseCode = "404", description = "Debit or credit card not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionDto initiateTransfer(@Valid @RequestBody TransferRequest request) {
        log.info("Received transfer request: DebitCard={}, CreditCard={}, Amount={}",
                request.getDebitCardNumber(), request.getCreditCardNumber(), request.getAmount());

        TransactionDto result = transactionService.transfer(
                request.getDebitCardNumber(),
                request.getCreditCardNumber(),
                request.getAmount()
        );

        log.info("Transfer completed with transaction ID: {}", result.getTransactionId());
        return result;
    }

    @Operation(summary = "Get my transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of transactions"),
            @ApiResponse(responseCode = "401", description = "User not authenticated"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/my-transactions")
    @PreAuthorize("hasRole('USER')")
    public Page<TransactionDto> getMyTransactions(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        log.info("Fetching transactions for current user (Page: {}, Size: {})", page, size);
        return transactionService.getTransactionsByCurrentUser(page, size);
    }

    // ==================== ADMIN ENDPOINTS ====================

    @Operation(summary = "Get all transactions (Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of all transactions"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<TransactionDto> getAllTransactions(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        log.info("Admin fetching all transactions (Page: {}, Size: {})", page, size);
        return transactionService.getAllTransactions(page, size);
    }

    @Operation(summary = "Get transactions by customer ID (Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of transactions for the customer"),
            @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/admin/customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<TransactionDto> getTransactionsByCustomerId(
            @Parameter(description = "ID of the customer to retrieve transactions for", required = true, example = "1")
            @PathVariable Integer customerId,
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10", required = false) Integer size
    ) {
        log.info("Admin fetching transactions for customer ID: {} (Page: {}, Size: {})", customerId, page, size);
        return transactionService.getTransactionsByCustomerId(customerId, page, size);
    }
}