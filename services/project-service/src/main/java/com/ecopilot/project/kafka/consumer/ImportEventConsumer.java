package com.ecopilot.project.kafka.consumer;

import com.ecopilot.project.kafka.event.ImportCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImportEventConsumer {

    @KafkaListener(topics = "import.jobs", groupId = "project-group")
    public void handleImportCompletedEvent(ImportCompletedEvent event) {
        log.info("Received IMPORT_COMPLETED event for project: {}", event.getProjectId());

        try {
            if ("SUCCESS".equals(event.getStatus())) {
                log.info("Import successful for project {}: {} lots, {} ouvrages created",
                        event.getProjectId(), event.getLotsCreated(), event.getOuvragesCreated());
                
                // TODO: Update project status or trigger additional processing
                // Example: projetService.updateProjectAfterImport(event.getProjectId());
            } else {
                log.error("Import failed for project {}: {}", event.getProjectId(), event.getErrorMessage());
                
                // TODO: Handle import failure (notify user, update project status)
            }
        } catch (Exception e) {
            log.error("Error processing import event for project: {}", event.getProjectId(), e);
        }
    }
}
