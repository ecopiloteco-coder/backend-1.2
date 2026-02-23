package com.ecopilot.user.kafka.consumer;

import com.ecopilot.user.kafka.event.ProjectEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectEventConsumer {

    @KafkaListener(topics = "project.events", groupId = "user-group")
    public void handleProjectAssignedEvent(ProjectEvent event) {
        log.info("Received {} event for project: {}", event.getEventType(), event.getProjectId());

        try {
            if ("PROJECT_ASSIGNED".equals(event.getEventType())) {
                log.info("User {} assigned to project {}: {}",
                        event.getUserId(), event.getProjectId(), event.getProjectName());
                
                // TODO: Update user statistics or send notification
                // Example: userService.updateProjectStats(event.getUserId(), event.getProjectId());
            }
        } catch (Exception e) {
            log.error("Error processing project event for user: {}", event.getUserId(), e);
        }
    }
}
