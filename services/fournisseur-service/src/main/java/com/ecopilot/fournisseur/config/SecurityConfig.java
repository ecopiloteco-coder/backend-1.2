package com.ecopilot.fournisseur.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for fournisseur-service.
 * JWT validation is delegated to Keycloak via JWKS endpoint.
 * All endpoints require authentication except health/actuator.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwksUri;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable) // CORS handled by API Gateway
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // Actuator endpoints — open for health checks
                .requestMatchers("/actuator/**").permitAll()
                // GET fournisseurs — accessible to authenticated users (admin or supplier)
                .requestMatchers(HttpMethod.GET, "/api/fournisseurs/**").authenticated()
                // Write ops — require authentication
                .requestMatchers(HttpMethod.POST, "/api/fournisseurs/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/fournisseurs/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/fournisseurs/**").authenticated()
                // All other endpoints require a valid JWT
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwksUri).build();
    }
}
