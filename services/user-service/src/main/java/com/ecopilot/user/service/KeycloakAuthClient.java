package com.ecopilot.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

/**
 * Client for Keycloak token operations (login, refresh).
 * All token validation is handled by Spring Security OAuth2 Resource Server.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KeycloakAuthClient {

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.resource}")
    private String clientId;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;

    /**
     * Authenticate a user and obtain Keycloak tokens.
     *
     * @return token response map containing access_token, refresh_token, expires_in
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> authenticate(String email, String password) {
        final String tokenUrl = buildTokenUrl();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("username", email);
        formData.add("password", password);

        return callTokenEndpoint(tokenUrl, formData);
    }

    /**
     * Refresh an access token using a refresh token.
     *
     * @return token response map containing new access_token, refresh_token, expires_in
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> refreshToken(String refreshToken) {
        final String tokenUrl = buildTokenUrl();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "refresh_token");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("refresh_token", refreshToken);

        return callTokenEndpoint(tokenUrl, formData);
    }

    private String buildTokenUrl() {
        return keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callTokenEndpoint(String tokenUrl, MultiValueMap<String, String> formData) {
        try {
            Map<String, Object> response = WebClient.create()
                    .post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("access_token")) {
                throw new RuntimeException("Missing access_token in Keycloak response");
            }

            return response;

        } catch (WebClientResponseException e) {
            log.error("Keycloak token request failed: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Keycloak authentication failed: " + e.getStatusCode(), e);
        } catch (Exception e) {
            log.error("Keycloak token request error: {}", e.getMessage());
            throw new RuntimeException("Authentication failed", e);
        }
    }
}
