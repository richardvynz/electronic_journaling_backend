package com.vinz.ej_copier.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonFormat
@AllArgsConstructor
@NoArgsConstructor
public class FootageMetadata {

    private String processingId;
    private String fileName;
    private String terminalId;
    private String pan;
    private String timestamp;
//    private TerminalStatus status;
    private String imageUrl;
//    private Condition condition;
}

