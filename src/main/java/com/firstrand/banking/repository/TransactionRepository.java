package com.firstrand.banking.repository;

import com.firstrand.banking.model.Account;
import com.firstrand.banking.model.Transaction;
import com.firstrand.banking.model.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findBySourceAccount(Account account);
    
    List<Transaction> findByDestinationAccount(Account account);
    
    Page<Transaction> findBySourceAccountOrDestinationAccount(
            Account sourceAccount, Account destinationAccount, Pageable pageable);
    
    List<Transaction> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    List<Transaction> findByType(TransactionType type);
}
