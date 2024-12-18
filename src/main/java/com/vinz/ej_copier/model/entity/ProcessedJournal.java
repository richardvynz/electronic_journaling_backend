package com.vinz.ej_copier.model.entity;

import com.vinz.ej_copier.model.dto.Meta;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@Builder
@Document(collection = "ej_copy")
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedJournal {
    @Id
    private String id;
    private Meta meta;
    private Double amountWithdrawn;
    private String cardNumber;
    private String transactionSequence;
    private LocalDate transactionDate;
    private String opCode;
    private String transactionType;
}