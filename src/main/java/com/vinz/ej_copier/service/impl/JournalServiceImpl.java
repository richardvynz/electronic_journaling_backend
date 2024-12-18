package com.vinz.ej_copier.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinz.ej_copier.config.ReactiveKafkaConfig;
import com.vinz.ej_copier.model.entity.ProcessedJournal;
import com.vinz.ej_copier.repository.JournalRepository;
import com.vinz.ej_copier.service.JournalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JournalServiceImpl implements JournalService {
    private final JournalRepository journalRepository;
    private final ReactiveKafkaProducerTemplate<String, String> reactiveKafkaProducerTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Boolean> processAndSaveJournal(ProcessedJournal journal) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(journal))
                .flatMap(journalData ->
                        reactiveKafkaProducerTemplate.send(ReactiveKafkaConfig.JOURNAL_TOPIC,
                                journal.getMeta().getTerminalId(),
                                journalData))
                .map(result -> true)
                .doOnSuccess(result -> log.info("Journal Successfully sent to Kafka with terminal: {}",
                        journal.getMeta().getTerminalId()))
                .doOnError(error -> log.error("Error sending journal to Kafka: {}", error.getMessage()))
                .onErrorReturn(false);
    }


    @Override
    public Mono<List<ProcessedJournal>> findJournalsByTerminalId(String terminalId) {
        return journalRepository.findByMetaTerminalId(terminalId)
                .collectList()
                .doOnError(error -> log.error("Error fetching journal: {}", error.getMessage()));
    }

    @Override
    public Mono<List<ProcessedJournal>> findJournalsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return journalRepository.findByMetaLocalDateTimeBetween(startDate, endDate)
                .collectList()
                .doOnError(error -> log.error("Error fetching journals: {}", error.getMessage()));
    }
}