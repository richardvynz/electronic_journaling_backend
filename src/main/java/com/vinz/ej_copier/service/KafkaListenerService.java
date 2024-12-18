package com.vinz.ej_copier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinz.ej_copier.config.ReactiveKafkaConfig;
import com.vinz.ej_copier.model.dto.EjCopy;
import com.vinz.ej_copier.model.entity.ProcessedJournal;
import com.vinz.ej_copier.model.dto.FootageMetadata;
import com.vinz.ej_copier.repository.JournalRepository;
import com.vinz.ej_copier.utiity.JournalDataParser;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private final ReactiveKafkaConsumerTemplate<String, String> kafkaConsumer;
    private final JournalRepository journalRepository;
    private final ObjectMapper objectMapper;
    private final JournalDataParser journalDataParser;
    private final Set<String> recentMessages = ConcurrentHashMap.newKeySet();
    private static final long MESSAGE_EXPIRY_TIME = 60000;

    @PostConstruct
    public void init() {
        log.info("Initializing Kafka listener for topics: {} and {}",
                ReactiveKafkaConfig.FOOTAGE_TOPIC,
                ReactiveKafkaConfig.JOURNAL_TOPIC);
        scheduleMessageCleanup();
        consumeMessages();
    }

    private void scheduleMessageCleanup() {
        Executors.newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(this::cleanupOldMessages,
                        MESSAGE_EXPIRY_TIME, MESSAGE_EXPIRY_TIME, TimeUnit.MILLISECONDS);
    }

    private void cleanupOldMessages() {
        recentMessages.clear();
    }

    private boolean isNewMessage(String message) {
        return recentMessages.add(message);
    }


    private void consumeMessages() {
        kafkaConsumer.receive()
                .doOnNext(record -> log.info("Received message from topic: {}, value: {}",
                        record.topic(), record.value()))
//                .filter(record -> isNewMessage(record.value()))
                .filter(record -> {
                    boolean isNew = isNewMessage(record.value());
                    log.info("Message is new: {}", isNew);
                    return isNew;
                })
                .doOnNext(record -> log.info("Message passed duplicate filter"))
                .flatMap(record -> {
                    String topic = record.topic();
                    String value = record.value();

                    if (topic.equals(ReactiveKafkaConfig.FOOTAGE_TOPIC)) {
                        return processFootageMessage(value);
                    } else if (topic.equals(ReactiveKafkaConfig.JOURNAL_TOPIC)) {
                        log.info("Processing journal message");
                        return processJournalMessage(value);
                    }
                    return Mono.empty();
                })
                .doOnSubscribe(sub -> log.info("Kafka consumer started"))
                .onErrorContinue((error, obj) -> {
                    log.error("Error processing message: {}", error.getMessage(), error);
                })
                .subscribe();
    }

    private Mono<FootageMetadata> processFootageMessage(String message) {
        // Implementation for footage processing
        return null;
    }

    private Mono<ProcessedJournal> processJournalMessage(String message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message, ProcessedJournal.class))
                .flatMap(processedJournal -> {
                    log.info("Saving processed journal to database: {}", processedJournal.getMeta().getTerminalId());
                    return journalRepository.save(processedJournal);
                })
                .doOnSuccess(saved -> log.info("Successfully persisted journal to database: {}",
                        saved.getMeta().getTerminalId()))
                .doOnError(error -> log.error("Error persisting journal to database: {}", error.getMessage()));
    }
//    private Mono<ProcessedJournal> processJournalMessage(String message) {
//        try {
//            EjCopy rawJournal = objectMapper.readValue(message, EjCopy.class);
//
//            ProcessedJournal processedJournal = journalDataParser.parseJournalData(rawJournal);
//
//            log.info("just before storing the Processed journal: {}", processedJournal);
//            return journalRepository.save(processedJournal)
//                    .doOnSuccess(saved -> log.info("Successfully saved journal entry for terminal: {}",
//                            processedJournal.getMeta().getTerminalId()))
//                    .doOnError(error -> log.error("Error saving journal entry: {}", error.getMessage()));
//        } catch (JsonProcessingException e) {
//            log.error("Error parsing journal message: {}", e.getMessage());
//            return Mono.empty();
//        }
//    }
}