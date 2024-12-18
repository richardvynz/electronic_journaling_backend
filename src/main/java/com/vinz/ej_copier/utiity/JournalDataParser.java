package com.vinz.ej_copier.utiity;

import com.vinz.ej_copier.model.dto.EjCopy;
import com.vinz.ej_copier.model.entity.ProcessedJournal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//@Slf4j
//@Component
//public class JournalDataParser {
//    // Updated patterns to be more flexible
//    private static final Pattern AMOUNT_PATTERN = Pattern.compile("Amount\\t+\\[(\\d+)\\]");
//    private static final Pattern CARD_PATTERN = Pattern.compile("Card Number\\t+\\[([0-9X*]+)\\]");
//    private static final Pattern SEQ_PATTERN = Pattern.compile("Trans SEQ number\\t+\\[(\\d+)\\]");
//    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})\\s+\\d{2}:\\d{2}:\\d{2}");
//    private static final Pattern OPCODE_PATTERN = Pattern.compile("OPCode\\t+\\[([\\w\\s]+)\\]");
//
//    // Alternative patterns for different formats
//    private static final Pattern ALT_CARD_PATTERN = Pattern.compile("(\\d{6}[*X]+\\d{4})\\s+\\d+");
//    private static final Pattern ALT_DATE_PATTERN = Pattern.compile("(\\d{2}\\\\\\d{2}\\\\\\d{2})\\s+\\d{2}:\\d{2}");
//
//    public ProcessedJournal parseJournalData(EjCopy rawJournal) {
//        ProcessedJournal processedJournal = new ProcessedJournal();
//        String rawData = rawJournal.getData();
//
//        // Check for empty or null data
//        if (rawData == null || rawData.trim().isEmpty()) {
//            log.info("Empty or null journal data for file: {}",
//                    rawJournal.getMeta() != null ? rawJournal.getMeta().getFileName() : "unknown");
//            processedJournal.setMeta(rawJournal.getMeta());
//            return processedJournal;
//        }
//
//        log.debug("Starting to parse journal data for file: {}",
//                rawJournal.getMeta() != null ? rawJournal.getMeta().getFileName() : "unknown");
//
//        try {
//            processedJournal.setMeta(rawJournal.getMeta());
//
//            // Find transaction section with more flexible boundaries
//            String transactionSection = extractTransactionSection(rawData);
//
//            // Parse date (try both formats)
//            parseDate(rawData, processedJournal);
//
//            // Only try to parse other fields if we have a transaction section
//            if (transactionSection != null) {
//                // Parse amount
//                parseAmount(transactionSection, processedJournal);
//
//                // Parse card number (try both formats)
//                parseCardNumber(transactionSection, rawData, processedJournal);
//
//                // Parse sequence number
//                parseSequenceNumber(transactionSection, processedJournal);
//
//                // Parse OpCode
//                parseOpCode(transactionSection, processedJournal);
//
//                // Determine transaction type
//                determineTransactionType(transactionSection, processedJournal);
//            }
//
//            logParsedData(processedJournal);
//
//        } catch (Exception e) {
//            log.error("Error parsing journal data: {}", e.getMessage(), e);
//        }
//
//        return processedJournal;
//    }
//
//    private String extractTransactionSection(String rawData) {
//        int startIndex = rawData.indexOf("[TRANSACTION RECORD]");
//        if (startIndex == -1) {
//            log.debug("No transaction record section found");
//            return null;
//        }
//
//        int endIndex = rawData.indexOf("JPR CONTENTS", startIndex);
//        if (endIndex == -1) {
//            endIndex = rawData.length();
//        }
//
//        return rawData.substring(startIndex, endIndex).trim();
//    }
//
//    private void parseDate(String rawData, ProcessedJournal journal) {
//        try {
//            Matcher standardDateMatcher = DATE_PATTERN.matcher(rawData);
//            if (standardDateMatcher.find()) {
//                String dateStr = standardDateMatcher.group(1);
//                journal.setTransactionDate(LocalDate.parse(dateStr,
//                        DateTimeFormatter.ofPattern("dd/MM/yyyy")));
//                return;
//            }
//
//            Matcher altDateMatcher = ALT_DATE_PATTERN.matcher(rawData);
//            if (altDateMatcher.find()) {
//                String dateStr = altDateMatcher.group(1).replace('\\', '/') + "20";
//                journal.setTransactionDate(LocalDate.parse(dateStr,
//                        DateTimeFormatter.ofPattern("dd/MM/yyyy")));
//            }
//        } catch (Exception e) {
//            log.debug("Could not parse date: {}", e.getMessage());
//        }
//    }
//
//    private void parseAmount(String section, ProcessedJournal journal) {
//        try {
//            Matcher matcher = AMOUNT_PATTERN.matcher(section);
//            if (matcher.find()) {
//                String amountStr = matcher.group(1);
//                if (!amountStr.equals("000000000000")) {  // Skip zero amounts
//                    journal.setAmountWithdrawn(Double.parseDouble(amountStr) / 100.0);
//                }
//            }
//        } catch (Exception e) {
//            log.debug("Could not parse amount: {}", e.getMessage());
//        }
//    }
//
//    private void parseCardNumber(String section, String rawData, ProcessedJournal journal) {
//        try {
//            // Try standard format first
//            Matcher matcher = CARD_PATTERN.matcher(section);
//            if (matcher.find()) {
//                journal.setCardNumber(matcher.group(1).trim());
//                return;
//            }
//
//            // Try alternative format
//            matcher = ALT_CARD_PATTERN.matcher(rawData);
//            if (matcher.find()) {
//                journal.setCardNumber(matcher.group(1).trim());
//            }
//        } catch (Exception e) {
//            log.debug("Could not parse card number: {}", e.getMessage());
//        }
//    }
//
//    private void parseSequenceNumber(String section, ProcessedJournal journal) {
//        try {
//            Matcher matcher = SEQ_PATTERN.matcher(section);
//            if (matcher.find()) {
//                journal.setTransactionSequence(matcher.group(1).trim());
//            }
//        } catch (Exception e) {
//            log.debug("Could not parse sequence number: {}", e.getMessage());
//        }
//    }
//
//    private void parseOpCode(String section, ProcessedJournal journal) {
//        try {
//            Matcher matcher = OPCODE_PATTERN.matcher(section);
//            if (matcher.find()) {
//                journal.setOpCode(matcher.group(1).trim());
//            }
//        } catch (Exception e) {
//            log.debug("Could not parse OpCode: {}", e.getMessage());
//        }
//    }
//
//    private void determineTransactionType(String section, ProcessedJournal journal) {
//        if (section.contains("CASH DISPENSE") ||
//                (section.contains("COMPLETED") && section.contains("CASH TAKEN"))) {
//            journal.setTransactionType("WITHDRAW");
//        } else if (section.contains("INQUIRY")) {
//            journal.setTransactionType("INQUIRY");
//        }
//    }
//
//    private void logParsedData(ProcessedJournal journal) {
//        log.info("Completed parsing journal data: amount={}, cardNumber={}, sequence={}, date={}, opCode={}, type={}",
//                journal.getAmountWithdrawn(),
//                journal.getCardNumber(),
//                journal.getTransactionSequence(),
//                journal.getTransactionDate(),
//                journal.getOpCode(),
//                journal.getTransactionType());
//    }
//}
//
//@Slf4j
//@Component
//public class JournalDataParser {
//    private static final Pattern AMOUNT_PATTERN = Pattern.compile("Amount\\s*\\[([0-9]{12})]");
//    private static final Pattern CARD_PATTERN = Pattern.compile("Card Number\\s*\\[([0-9X]+)]");
//    private static final Pattern SEQ_PATTERN = Pattern.compile("Trans SEQ number\\s*\\[([0-9]+)]");
//    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2}/\\d{2}/\\d{2}\\d{2})\\s+\\d{2}:\\d{2}:\\d{2}");
//    private static final Pattern OPCODE_PATTERN = Pattern.compile("OPCode\\s*\\[([\\w\\s]+)]");
//
//    public ProcessedJournal parseJournalData(EjCopy rawJournal) {
//        ProcessedJournal processedJournal = new ProcessedJournal();
//        String rawData = rawJournal.getData();
//
//        // Debug log the input
//        log.info("Starting to parse journal data for file: {}", rawJournal.getMeta().getFileName());
//        log.debug("Raw journal data: {}", rawData);
//
//        try {
//            // Set metadata first
//            processedJournal.setMeta(rawJournal.getMeta());
//            log.debug("Set metadata: terminalId={}, batchNumber={}",
//                    rawJournal.getMeta().getTerminalId(),
//                    rawJournal.getMeta().getBatchNumber());
//
//            // Find transaction section
//            int startIndex = rawData.indexOf("[TRANSACTION RECORD]");
//            int endIndex = rawData.indexOf("JPR CONTENTS");
//            if (startIndex == -1 || endIndex == -1) {
//                log.warn("Transaction section not found in data. startIndex={}, endIndex={}", startIndex, endIndex);
//                return processedJournal;
//            }
//
//            String transactionSection = rawData.substring(startIndex, endIndex).trim();
//            log.debug("Extracted transaction section: {}", transactionSection);
//
//            // Parse date
//            Matcher dateMatcher = DATE_PATTERN.matcher(rawData);
//            if (dateMatcher.find()) {
//                String dateStr = dateMatcher.group(1);
//                log.debug("Found date string: {}", dateStr);
//                try {
//                    processedJournal.setTransactionDate(LocalDate.parse(dateStr,
//                            DateTimeFormatter.ofPattern("dd/MM/yyyy")));
//                    log.debug("Parsed transaction date: {}", processedJournal.getTransactionDate());
//                } catch (Exception e) {
//                    log.error("Failed to parse date '{}': {}", dateStr, e.getMessage());
//                }
//            } else {
//                log.warn("No date found in transaction data");
//            }
//
//            // Parse amount
//            Matcher amountMatcher = AMOUNT_PATTERN.matcher(transactionSection);
//            if (amountMatcher.find()) {
//                String amountStr = amountMatcher.group(1);
//                log.debug("Found amount string: {}", amountStr);
//                try {
//                    processedJournal.setAmountWithdrawn(Double.parseDouble(amountStr) / 100.0);
//                    log.debug("Parsed amount: {}", processedJournal.getAmountWithdrawn());
//                } catch (NumberFormatException e) {
//                    log.error("Failed to parse amount '{}': {}", amountStr, e.getMessage());
//                }
//            } else {
//                log.warn("No amount found in transaction section");
//            }
//
//            // Parse card number
//            Matcher cardMatcher = CARD_PATTERN.matcher(transactionSection);
//            if (cardMatcher.find()) {
//                String cardNumber = cardMatcher.group(1).trim();
//                processedJournal.setCardNumber(cardNumber);
//                log.debug("Found card number: {}", cardNumber);
//            } else {
//                log.warn("No card number found in transaction section");
//            }
//
//            // Parse sequence number
//            Matcher seqMatcher = SEQ_PATTERN.matcher(transactionSection);
//            if (seqMatcher.find()) {
//                String seqNumber = seqMatcher.group(1).trim();
//                processedJournal.setTransactionSequence(seqNumber);
//                log.debug("Found sequence number: {}", seqNumber);
//            } else {
//                log.warn("No sequence number found in transaction section");
//            }
//
//            // Parse OpCode
//            Matcher opCodeMatcher = OPCODE_PATTERN.matcher(transactionSection);
//            if (opCodeMatcher.find()) {
//                String opCode = opCodeMatcher.group(1).trim();
//                processedJournal.setOpCode(opCode);
//                log.debug("Found OpCode: {}", opCode);
//            } else {
//                log.warn("No OpCode found in transaction section");
//            }
//
//            // Determine transaction type
//            if (transactionSection.contains("CASH DISPENSE") ||
//                    (transactionSection.contains("COMPLETED") && transactionSection.contains("CASH TAKEN"))) {
//                processedJournal.setTransactionType("WITHDRAW");
//                log.debug("Set transaction type to WITHDRAW");
//            } else {
//                log.warn("Could not determine transaction type");
//            }
//
//            // Log final parsed data
//            log.info("Completed parsing journal data: amount={}, cardNumber={}, sequence={}, date={}, opCode={}, type={}",
//                    processedJournal.getAmountWithdrawn(),
//                    processedJournal.getCardNumber(),
//                    processedJournal.getTransactionSequence(),
//                    processedJournal.getTransactionDate(),
//                    processedJournal.getOpCode(),
//                    processedJournal.getTransactionType());
//
//        } catch (Exception e) {
//            log.error("Error parsing journal data: {}", e.getMessage(), e);
//        }
//
//        return processedJournal;
//    }}

@Slf4j
@Component
public class JournalDataParser {
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("Amount\\s*\\[([0-9]{12})]");
    private static final Pattern CARD_PATTERN = Pattern.compile("Card Number\\s*\\[([0-9X]+)]");
    private static final Pattern SEQ_PATTERN = Pattern.compile("Trans SEQ number\\s*\\[([0-9]+)]");
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{2}/\\d{2}/\\d{4})\\s+\\d{2}:\\d{2}:\\d{2}");
    private static final Pattern OPCODE_PATTERN = Pattern.compile("OPCode\\s*\\[([\\w\\s]+)]");

    public ProcessedJournal parseJournalData(EjCopy rawJournal) {
        ProcessedJournal processedJournal = new ProcessedJournal();
        String rawData = rawJournal.getData();

        // Debug log the input
        log.info("Starting to parse journal data for file: {}", rawJournal.getMeta().getFileName());
        log.debug("Raw journal data: {}", rawData);

        try {
            // Set metadata first
            processedJournal.setMeta(rawJournal.getMeta());

            // Find transaction section
            int startIndex = rawData.indexOf("[TRANSACTION RECORD]");
            if (startIndex == -1) {
                log.warn("Transaction section not found in data");
                return processedJournal;
            }

            String transactionSection = rawData.substring(startIndex);
            log.debug("Extracted transaction section: {}", transactionSection);

            // Parse amount
            Matcher amountMatcher = AMOUNT_PATTERN.matcher(transactionSection);
            if (amountMatcher.find()) {
                String amountStr = amountMatcher.group(1);
                log.debug("Found amount string: {}", amountStr);
                try {
                    double amount = Double.parseDouble(amountStr) / 100.0; // Convert to actual currency value
                    processedJournal.setAmountWithdrawn(amount);
                    log.debug("Parsed amount: {}", amount);
                } catch (NumberFormatException e) {
                    log.error("Failed to parse amount '{}': {}", amountStr, e.getMessage());
                }
            }

            // Parse card number
            Matcher cardMatcher = CARD_PATTERN.matcher(transactionSection);
            if (cardMatcher.find()) {
                String cardNumber = cardMatcher.group(1).trim();
                processedJournal.setCardNumber(cardNumber);
                log.debug("Found card number: {}", cardNumber);
            }

            // Parse sequence number
            Matcher seqMatcher = SEQ_PATTERN.matcher(transactionSection);
            if (seqMatcher.find()) {
                String seqNumber = seqMatcher.group(1).trim();
                processedJournal.setTransactionSequence(seqNumber);
                log.debug("Found sequence number: {}", seqNumber);
            }

            // Parse date - look for first date occurrence in the raw data
            Matcher dateMatcher = DATE_PATTERN.matcher(rawData);
            if (dateMatcher.find()) {
                String dateStr = dateMatcher.group(1);
                try {
                    LocalDate date = LocalDate.parse(dateStr,
                            DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    processedJournal.setTransactionDate(date);
                    log.debug("Parsed transaction date: {}", date);
                } catch (Exception e) {
                    log.error("Failed to parse date '{}': {}", dateStr, e.getMessage());
                }
            }

            // Parse OpCode
            Matcher opCodeMatcher = OPCODE_PATTERN.matcher(transactionSection);
            if (opCodeMatcher.find()) {
                String opCode = opCodeMatcher.group(1).trim();
                processedJournal.setOpCode(opCode);
                log.debug("Found OpCode: {}", opCode);
            }

            // Determine transaction type based on multiple indicators
            if (rawData.contains("CASH DISPENSE") &&
                    rawData.contains("CASH TAKEN") &&
                    rawData.contains("TRANSACTION DATA (COMPLETED)")) {
                processedJournal.setTransactionType("WITHDRAW");
                log.debug("Set transaction type to WITHDRAW");
            }

            // Validate parsed data
            validateParsedData(processedJournal);

            log.info("Successfully parsed journal: amount={}, cardNumber={}, sequence={}, date={}, opCode={}, type={}",
                    processedJournal.getAmountWithdrawn(),
                    processedJournal.getCardNumber(),
                    processedJournal.getTransactionSequence(),
                    processedJournal.getTransactionDate(),
                    processedJournal.getOpCode(),
                    processedJournal.getTransactionType());

        } catch (Exception e) {
            log.error("Error parsing journal data: {}", e.getMessage(), e);
        }

        return processedJournal;
    }

    private void validateParsedData(ProcessedJournal journal) {
        if (journal.getAmountWithdrawn() == null) {
            log.warn("Amount not parsed for journal: {}", journal.getMeta().getFileName());
        }
        if (journal.getCardNumber() == null) {
            log.warn("Card number not parsed for journal: {}", journal.getMeta().getFileName());
        }
        if (journal.getTransactionSequence() == null) {
            log.warn("Transaction sequence not parsed for journal: {}", journal.getMeta().getFileName());
        }
        if (journal.getTransactionDate() == null) {
            log.warn("Transaction date not parsed for journal: {}", journal.getMeta().getFileName());
        }
        if (journal.getOpCode() == null) {
            log.warn("OpCode not parsed for journal: {}", journal.getMeta().getFileName());
        }
        if (journal.getTransactionType() == null) {
            log.warn("Transaction type not determined for journal: {}", journal.getMeta().getFileName());
        }
    }
}