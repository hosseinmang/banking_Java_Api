package com.firstrand.banking.service;

import com.firstrand.banking.exception.InsufficientFundsException;
import com.firstrand.banking.exception.ResourceNotFoundException;
import com.firstrand.banking.model.Account;
import com.firstrand.banking.model.Transaction;
import com.firstrand.banking.model.TransactionStatus;
import com.firstrand.banking.model.TransactionType;
import com.firstrand.banking.model.dto.TransferRequest;
import com.firstrand.banking.repository.AccountRepository;
import com.firstrand.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private AccountService accountService;
    
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }
    
    public List<Transaction> getTransactionsBySourceAccount(Account account) {
        return transactionRepository.findBySourceAccount(account);
    }
    
    public List<Transaction> getTransactionsByDestinationAccount(Account account) {
        return transactionRepository.findByDestinationAccount(account);
    }
    
    public Page<Transaction> getTransactionsByAccount(Account account, Pageable pageable) {
        return transactionRepository.findBySourceAccountOrDestinationAccount(account, account, pageable);
    }
    
    @Transactional
    public Transaction transferFunds(TransferRequest transferRequest) {
        // Get source and destination accounts
        Account sourceAccount = accountService.getAccountByAccountNumber(transferRequest.getSourceAccountNumber());
        Account destinationAccount = accountService.getAccountByAccountNumber(transferRequest.getDestinationAccountNumber());
        
        // Check if source account has sufficient funds
        if (sourceAccount.getBalance().compareTo(transferRequest.getAmount()) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account: " + sourceAccount.getAccountNumber());
        }
        
        // Create a transaction record
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(sourceAccount);
        transaction.setDestinationAccount(destinationAccount);
        transaction.setAmount(transferRequest.getAmount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setReference(transferRequest.getReference());
        transaction.setDescription(transferRequest.getDescription());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.COMPLETED);
        
        // Update account balances
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(transferRequest.getAmount()));
        destinationAccount.setBalance(destinationAccount.getBalance().add(transferRequest.getAmount()));
        
        // Save changes
        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
        
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public Transaction deposit(String accountNumber, BigDecimal amount, String reference, String description) {
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        
        // Create a transaction record
        Transaction transaction = new Transaction();
        transaction.setDestinationAccount(account);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.DEPOSIT);
        transaction.setReference(reference);
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.COMPLETED);
        
        // Update account balance
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
        
        return transactionRepository.save(transaction);
    }
    
    @Transactional
    public Transaction withdraw(String accountNumber, BigDecimal amount, String reference, String description) {
        Account account = accountService.getAccountByAccountNumber(accountNumber);
        
        // Check if account has sufficient funds
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account: " + account.getAccountNumber());
        }
        
        // Create a transaction record
        Transaction transaction = new Transaction();
        transaction.setSourceAccount(account);
        transaction.setAmount(amount);
        transaction.setType(TransactionType.WITHDRAWAL);
        transaction.setReference(reference);
        transaction.setDescription(description);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.COMPLETED);
        
        // Update account balance
        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);
        
        return transactionRepository.save(transaction);
    }
}
