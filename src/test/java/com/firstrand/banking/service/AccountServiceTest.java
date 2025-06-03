package com.firstrand.banking.service;

import com.firstrand.banking.exception.ResourceNotFoundException;
import com.firstrand.banking.model.Account;
import com.firstrand.banking.model.AccountType;
import com.firstrand.banking.model.User;
import com.firstrand.banking.model.dto.AccountRequest;
import com.firstrand.banking.repository.AccountRepository;
import com.firstrand.banking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private User testUser;
    private Account testAccount;
    private AccountRequest accountRequest;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        // Setup test account
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setAccountNumber("1234567890");
        testAccount.setAccountType(AccountType.SAVINGS);
        testAccount.setBalance(BigDecimal.valueOf(1000));
        testAccount.setCreatedAt(LocalDateTime.now());
        testAccount.setUpdatedAt(LocalDateTime.now());
        testAccount.setAccountName("Test Savings");
        testAccount.setActive(true);
        testAccount.setUser(testUser);

        // Setup account request
        accountRequest = new AccountRequest();
        accountRequest.setAccountName("Test Account");
        accountRequest.setAccountType(AccountType.CHECKING);
    }

    @Test
    void getAllAccounts_ShouldReturnAllAccounts() {
        // Arrange
        List<Account> accounts = new ArrayList<>();
        accounts.add(testAccount);
        when(accountRepository.findAll()).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAllAccounts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAccount.getAccountNumber(), result.get(0).getAccountNumber());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void getAccountsByUser_ShouldReturnUserAccounts() {
        // Arrange
        List<Account> accounts = new ArrayList<>();
        accounts.add(testAccount);
        when(accountRepository.findByUser(testUser)).thenReturn(accounts);

        // Act
        List<Account> result = accountService.getAccountsByUser(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAccount.getAccountNumber(), result.get(0).getAccountNumber());
        verify(accountRepository, times(1)).findByUser(testUser);
    }

    @Test
    void getAccountById_ShouldReturnAccount_WhenAccountExists() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        Account result = accountService.getAccountById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testAccount.getAccountNumber(), result.getAccountNumber());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void getAccountById_ShouldThrowException_WhenAccountDoesNotExist() {
        // Arrange
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getAccountById(999L);
        });
        verify(accountRepository, times(1)).findById(999L);
    }

    @Test
    void getAccountByAccountNumber_ShouldReturnAccount_WhenAccountExists() {
        // Arrange
        when(accountRepository.findByAccountNumber(testAccount.getAccountNumber())).thenReturn(Optional.of(testAccount));

        // Act
        Account result = accountService.getAccountByAccountNumber(testAccount.getAccountNumber());

        // Assert
        assertNotNull(result);
        assertEquals(testAccount.getAccountNumber(), result.getAccountNumber());
        verify(accountRepository, times(1)).findByAccountNumber(testAccount.getAccountNumber());
    }

    @Test
    void getAccountByAccountNumber_ShouldThrowException_WhenAccountDoesNotExist() {
        // Arrange
        when(accountRepository.findByAccountNumber("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            accountService.getAccountByAccountNumber("nonexistent");
        });
        verify(accountRepository, times(1)).findByAccountNumber("nonexistent");
    }

    @Test
    void createAccount_ShouldCreateAndReturnNewAccount() {
        // Arrange
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(accountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account savedAccount = invocation.getArgument(0);
            savedAccount.setId(1L);
            return savedAccount;
        });

        // Act
        Account result = accountService.createAccount("testuser", accountRequest);

        // Assert
        assertNotNull(result);
        assertEquals(accountRequest.getAccountName(), result.getAccountName());
        assertEquals(accountRequest.getAccountType(), result.getAccountType());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        assertEquals(testUser, result.getUser());
        assertTrue(result.isActive());
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void updateAccountBalance_ShouldUpdateAndReturnAccount() {
        // Arrange
        BigDecimal amountToAdd = BigDecimal.valueOf(500);
        BigDecimal expectedBalance = testAccount.getBalance().add(amountToAdd);
        
        when(accountRepository.findByAccountNumber(testAccount.getAccountNumber())).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Account result = accountService.updateAccountBalance(testAccount.getAccountNumber(), amountToAdd);

        // Assert
        assertNotNull(result);
        assertEquals(expectedBalance, result.getBalance());
        verify(accountRepository, times(1)).findByAccountNumber(testAccount.getAccountNumber());
        verify(accountRepository, times(1)).save(testAccount);
    }
}
