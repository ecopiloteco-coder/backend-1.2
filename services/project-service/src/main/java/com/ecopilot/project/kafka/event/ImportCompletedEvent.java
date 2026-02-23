package com.ecopilot.project.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportCompletedEvent {
    private Long projectId;
    private String fileName;
    private Integer lotsCreated;
    private Integer ouvragesCreated;
    private String status; // SUCCESS, FAILED
    private String errorMessage;
    private LocalDateTime timestamp;
}
