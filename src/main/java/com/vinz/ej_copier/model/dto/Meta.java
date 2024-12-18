package com.vinz.ej_copier.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Meta {
    private String fileName;
    private long batchNumber;
    private LocalDateTime localDateTime;
    private String terminalId;
}