package com.firstrand.banking.model.dto;

import com.firstrand.banking.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AccountRequest {
    
    @NotBlank
    @Size(min = 3, max = 100)
    private String accountName;
    
    @NotNull
    private AccountType accountType;
}
