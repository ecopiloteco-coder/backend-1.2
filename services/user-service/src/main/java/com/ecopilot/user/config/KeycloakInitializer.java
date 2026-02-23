package com.ecopilot.user.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.RealmRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakInitializer implements CommandLineRunner {

    private final Keycloak keycloak;

    @Value("${keycloak.realm:ecopilot}")
    private String realmName;

    @Override
    public void run(String... args) {
        log.info("Initializing Keycloak configuration for realm: {}", realmName);
        try {
            boolean realmExists = keycloak.realms().findAll().stream()
                    .anyMatch(r -> r.getRealm().equals(realmName));

            if (!realmExists) {
                log.info("Realm '{}' not found. Creating it...", realmName);
                RealmRepresentation realmRep = new RealmRepresentation();
                realmRep.setRealm(realmName);
                realmRep.setEnabled(true);
                realmRep.setRegistrationAllowed(true); // Optional but good for users
                
                keycloak.realms().create(realmRep);
                log.info("Realm '{}' created successfully.", realmName);
            } else {
                log.info("Realm '{}' already exists.", realmName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize Keycloak realm: {}", e.getMessage(), e);
            // We do not throw exception here to allow service to start, 
            // but functionality will be degraded if realm is missing.
        }
    }
}
