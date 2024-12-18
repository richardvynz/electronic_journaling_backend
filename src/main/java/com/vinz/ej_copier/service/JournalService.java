package com.vinz.ej_copier.service;

import com.vinz.ej_copier.model.entity.ProcessedJournal;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface JournalService {
    Mono<Boolean> processAndSaveJournal(ProcessedJournal journal);
    Mono<List<ProcessedJournal>> findJournalsByTerminalId(String terminalId);
    Mono<List<ProcessedJournal>> findJournalsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}