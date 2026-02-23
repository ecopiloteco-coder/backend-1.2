package com.ecopilot.user.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectEvent {
    private String eventType; // PROJECT_ASSIGNED
    private Long projectId;
    private String projectName;
    private Long userId;
    private LocalDateTime timestamp;
}
