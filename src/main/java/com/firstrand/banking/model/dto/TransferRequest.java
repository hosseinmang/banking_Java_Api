package com.firstrand.banking.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    
    @NotBlank
    private String sourceAccountNumber;
    
    @NotBlank
    private String destinationAccountNumber;
    
    @NotNull
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    private String reference;
    
    private String description;
}
