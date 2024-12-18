package com.vinz.ej_copier.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class JournalResponse {
    private Meta meta;
    private Double amountWithdrawn;
    private String cardNumber;
    private String transactionSequence;
    private LocalDate transactionDate;
    private String opCode;
    private String transactionType;
}
