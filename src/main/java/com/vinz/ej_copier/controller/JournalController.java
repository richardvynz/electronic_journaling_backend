package com.vinz.ej_copier.controller;

import com.vinz.ej_copier.model.dto.BaseResponse;
import com.vinz.ej_copier.model.dto.EjCopy;
import com.vinz.ej_copier.model.dto.JournalResponse;
import com.vinz.ej_copier.model.entity.ProcessedJournal;
import com.vinz.ej_copier.service.JournalService;
import com.vinz.ej_copier.utiity.EjCopyToResponseJournal;
import com.vinz.ej_copier.utiity.JournalDataParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/journals")
@RequiredArgsConstructor
@Slf4j
public class JournalController {
    private final JournalService journalService;
    private final JournalDataParser journalDataParser;

    @PostMapping
    public Mono<ResponseEntity<BaseResponse<JournalResponse>>> processJournal(@RequestBody EjCopy journal) {
        log.info("Received journal for terminal ID: {}", journal.getMeta().getTerminalId());
        ProcessedJournal processedJournal = journalDataParser.parseJournalData(journal);
        JournalResponse response = EjCopyToResponseJournal.processedJournalToJournalResponse(processedJournal);
        return journalService.processAndSaveJournal(processedJournal)
                .map(success -> ResponseEntity.ok(new BaseResponse<JournalResponse>(true,
                        "Journal sent to processing queue", response)))
                .onErrorReturn(ResponseEntity.internalServerError()
                        .body(new BaseResponse<>(false, "Failed to process journal", null)));
    }


    @GetMapping("/terminal/{terminalId}")
    public Mono<ResponseEntity<BaseResponse<List<ProcessedJournal>>>> getJournalsByTerminal(
            @PathVariable String terminalId) {
        return journalService.findJournalsByTerminalId(terminalId)
                .map(journals -> {
                    if (journals.isEmpty()) {
                        return ResponseEntity.ok(
                                new BaseResponse<List<ProcessedJournal>>(false, "No journals found for the terminal", null));
                    }
                    return ResponseEntity.ok(
                            new BaseResponse<>(true, "Journals fetched successfully", journals));
                })
                .onErrorReturn(ResponseEntity.internalServerError()
                        .body(new BaseResponse<>(false, "Failed to fetch journals due to error", null)));
    }

    @GetMapping("/date-range")
    public Mono<ResponseEntity<BaseResponse<List<ProcessedJournal>>>> getJournalsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return journalService.findJournalsByDateRange(startDate, endDate)
                .map(journals -> {
                    if (journals.isEmpty()) {
                        return ResponseEntity.ok(
                                new BaseResponse<List<ProcessedJournal>>(false, "No journals found in the date range", null));
                    }
                    return ResponseEntity.ok(
                            new BaseResponse<List<ProcessedJournal>>(true, "Journals fetched successfully", journals));
                })
                .onErrorReturn(ResponseEntity.internalServerError()
                        .body(new BaseResponse<List<ProcessedJournal>>(false, "Failed to fetch journals due to error", null)));
    }

}