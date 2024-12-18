package com.vinz.ej_copier.utiity;

import com.vinz.ej_copier.model.dto.JournalResponse;
import com.vinz.ej_copier.model.entity.ProcessedJournal;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EjCopyToResponseJournal {
    public JournalResponse processedJournalToJournalResponse(ProcessedJournal journal){
        return JournalResponse.builder()
                .meta(journal.getMeta())
                .amountWithdrawn(journal.getAmountWithdrawn())
                .cardNumber(journal.getCardNumber())
                .opCode(journal.getOpCode())
                .transactionSequence(journal.getTransactionSequence())
                .transactionType(journal.getTransactionType())
                .transactionDate(journal.getTransactionDate())
                .build();
    }
}
