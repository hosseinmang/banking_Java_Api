package com.firstrand.banking.service;

import com.firstrand.banking.exception.ResourceNotFoundException;
import com.firstrand.banking.model.Account;
import com.firstrand.banking.model.User;
import com.firstrand.banking.model.dto.AccountRequest;
import com.firstrand.banking.repository.AccountRepository;
import com.firstrand.banking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    public List<Account> getAccountsByUser(User user) {
        return accountRepository.findByUser(user);
    }
    
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }
    
    public Account getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with account number: " + accountNumber));
    }
    
    @Transactional
    public Account createAccount(String username, AccountRequest accountRequest) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
        
        Account account = new Account();
        account.setAccountNumber(generateAccountNumber());
        account.setAccountName(accountRequest.getAccountName());
        account.setAccountType(accountRequest.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setActive(true);
        account.setUser(user);
        
        return accountRepository.save(account);
    }
    
    @Transactional
    public Account updateAccountBalance(String accountNumber, BigDecimal amount) {
        Account account = getAccountByAccountNumber(accountNumber);
        account.setBalance(account.getBalance().add(amount));
        account.setUpdatedAt(LocalDateTime.now());
        
        return accountRepository.save(account);
    }
    
    private String generateAccountNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        
        // Generate a 10-digit account number
        for (int i = 0; i < 10; i++) {
            sb.append(random.nextInt(10));
        }
        
        String accountNumber = sb.toString();
        
        // Check if account number already exists, if yes, generate again
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            return generateAccountNumber();
        }
        
        return accountNumber;
    }
}
