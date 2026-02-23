package com.ecopilot.project.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final RestTemplate restTemplate;

    @Value("${notification.service.url:http://notification-service:8084}")
    private String notificationServiceUrl;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<String> getProjectEvents(
            @PathVariable String projectId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {

        String url = notificationServiceUrl + "/api/events/project/" + projectId;

        HttpHeaders headers = new HttpHeaders();
        if (authHeader != null && !authHeader.isEmpty()) {
            headers.set(HttpHeaders.AUTHORIZATION, authHeader);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}

