package com.firstrand.banking.controller;

import com.firstrand.banking.model.Account;
import com.firstrand.banking.model.Transaction;
import com.firstrand.banking.model.dto.MessageResponse;
import com.firstrand.banking.model.dto.TransferRequest;
import com.firstrand.banking.security.UserDetailsImpl;
import com.firstrand.banking.service.AccountService;
import com.firstrand.banking.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AccountService accountService;
    
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Page<Transaction>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String accountNumber) {
        
        UserDetailsImpl userDetails = getCurrentUser();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        
        if (accountNumber != null && !accountNumber.isEmpty()) {
            Account account = accountService.getAccountByAccountNumber(accountNumber);
            
            // Check if the account belongs to the authenticated user
            if (!account.getUser().getId().equals(userDetails.getId()) && !hasAdminRole()) {
                return ResponseEntity.status(403).build();
            }
            
            Page<Transaction> transactions = transactionService.getTransactionsByAccount(account, pageable);
            return ResponseEntity.ok(transactions);
        } else {
            // For admin, return all transactions, for regular users return an empty page
            if (hasAdminRole()) {
                return ResponseEntity.ok(Page.empty());
            } else {
                return ResponseEntity.status(403).build();
            }
        }
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        UserDetailsImpl userDetails = getCurrentUser();
        Transaction transaction = transactionService.getTransactionById(id);
        
        // Check if the transaction involves an account that belongs to the authenticated user
        boolean hasAccess = false;
        
        if (transaction.getSourceAccount() != null && 
            transaction.getSourceAccount().getUser().getId().equals(userDetails.getId())) {
            hasAccess = true;
        }
        
        if (transaction.getDestinationAccount() != null && 
            transaction.getDestinationAccount().getUser().getId().equals(userDetails.getId())) {
            hasAccess = true;
        }
        
        if (!hasAccess && !hasAdminRole()) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(transaction);
    }
    
    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> transferFunds(@Valid @RequestBody TransferRequest transferRequest) {
        UserDetailsImpl userDetails = getCurrentUser();
        
        // Check if source account belongs to the authenticated user
        Account sourceAccount = accountService.getAccountByAccountNumber(transferRequest.getSourceAccountNumber());
        if (!sourceAccount.getUser().getId().equals(userDetails.getId()) && !hasAdminRole()) {
            return ResponseEntity.status(403)
                    .body(new MessageResponse("You don't have permission to transfer from this account"));
        }
        
        Transaction transaction = transactionService.transferFunds(transferRequest);
        return ResponseEntity.ok(transaction);
    }
    
    @PostMapping("/deposit/{accountNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deposit(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String description) {
        
        UserDetailsImpl userDetails = getCurrentUser();
        
        // Check if account belongs to the authenticated user
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        if (!account.getUser().getId().equals(userDetails.getId()) && !hasAdminRole()) {
            return ResponseEntity.status(403)
                    .body(new MessageResponse("You don't have permission to deposit to this account"));
        }
        
        Transaction transaction = transactionService.deposit(accountNumber, amount, reference, description);
        return ResponseEntity.ok(transaction);
    }
    
    @PostMapping("/withdraw/{accountNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> withdraw(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String reference,
            @RequestParam(required = false) String description) {
        
        UserDetailsImpl userDetails = getCurrentUser();
        
        // Check if account belongs to the authenticated user
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        if (!account.getUser().getId().equals(userDetails.getId()) && !hasAdminRole()) {
            return ResponseEntity.status(403)
                    .body(new MessageResponse("You don't have permission to withdraw from this account"));
        }
        
        Transaction transaction = transactionService.withdraw(accountNumber, amount, reference, description);
        return ResponseEntity.ok(transaction);
    }
    
    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }
    
    private boolean hasAdminRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
    }
}
