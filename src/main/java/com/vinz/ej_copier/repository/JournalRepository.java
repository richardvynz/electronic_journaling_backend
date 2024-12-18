package com.vinz.ej_copier.repository;

import com.vinz.ej_copier.model.entity.ProcessedJournal;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface JournalRepository extends ReactiveMongoRepository<ProcessedJournal, String> {

    @Query("{'meta.terminalId': ?0}")
    Flux<ProcessedJournal> findByMetaTerminalId(String terminalId);

    @Query("{'meta.localDateTime': {$gte: ?0, $lte: ?1}}")
    Flux<ProcessedJournal> findByMetaLocalDateTimeBetween(LocalDateTime startDate, LocalDateTime endDate);
}
