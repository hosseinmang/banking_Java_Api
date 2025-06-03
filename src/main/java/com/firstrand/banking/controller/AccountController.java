package com.firstrand.banking.controller;

import com.firstrand.banking.model.Account;
import com.firstrand.banking.model.User;
import com.firstrand.banking.model.dto.AccountRequest;
import com.firstrand.banking.model.dto.MessageResponse;
import com.firstrand.banking.security.UserDetailsImpl;
import com.firstrand.banking.service.AccountService;
import com.firstrand.banking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    @Autowired
    private UserService userService;    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<Account>> getAllAccounts() {
        UserDetailsImpl userDetails = getCurrentUser();
        
        // Get the user from the repository, then find their accounts
        User user = userService.getUserByUsername(userDetails.getUsername());
        List<Account> accounts = accountService.getAccountsByUser(user);
        
        return ResponseEntity.ok(accounts);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        UserDetailsImpl userDetails = getCurrentUser();
        Account account = accountService.getAccountById(id);
        
        // Check if the account belongs to the authenticated user
        if (!account.getUser().getId().equals(userDetails.getId()) && !hasAdminRole()) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(account);
    }
    
    @GetMapping("/number/{accountNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Account> getAccountByAccountNumber(@PathVariable String accountNumber) {
        UserDetailsImpl userDetails = getCurrentUser();
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        
        // Check if the account belongs to the authenticated user
        if (!account.getUser().getId().equals(userDetails.getId()) && !hasAdminRole()) {
            return ResponseEntity.status(403).build();
        }
        
        return ResponseEntity.ok(account);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountRequest accountRequest) {
        UserDetailsImpl userDetails = getCurrentUser();
        Account account = accountService.createAccount(userDetails.getUsername(), accountRequest);
        return ResponseEntity.ok(account);
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
