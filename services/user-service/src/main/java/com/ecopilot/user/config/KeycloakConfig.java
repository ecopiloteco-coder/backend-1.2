package com.ecopilot.user.config;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.ws.rs.client.ClientBuilder;

/**
 * Keycloak Admin Client configuration.
 * Used only for user management operations (create/update users in Keycloak).
 * Authentication token validation is handled by Spring Security OAuth2 Resource Server.
 */
@Configuration
public class KeycloakConfig {

    @Value("${keycloak.auth-server-url:http://keycloak:8080}")
    private String serverUrl;

    @Value("${keycloak.realm:ecopilot}")
    private String realm;

    @Value("${keycloak.admin-username}")
    private String adminUsername;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm("master")
                .grantType("password")
                .clientId("admin-cli")
                .username(adminUsername)
                .password(adminPassword)
                .resteasyClient(
                    ((ResteasyClientBuilder) ClientBuilder.newBuilder())
                        .connectionPoolSize(10)
                        .build()
                )
                .build();
    }
}
