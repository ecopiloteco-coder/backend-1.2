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
public class ProjectEvent {
    private String eventType;
    private String action;
    private Long projectId;
    private String projectName;
    private String userId;
    private String timestamp;
    private String details;
    private Long projet;
    private Long lot;
    private Long ouvrage;
    private Long bloc;
    private Long article;
    private Object metadata;
}
