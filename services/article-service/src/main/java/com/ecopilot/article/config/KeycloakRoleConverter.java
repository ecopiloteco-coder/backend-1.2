package com.ecopilot.article.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        System.out.println("=== JWT CLAIMS ===");
        System.out.println("Subject : " + jwt.getClaimAsString("sub"));
        System.out.println("realm_access : " + jwt.getClaimAsMap("realm_access"));
        System.out.println("==================");
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            System.out.println("==> Aucun role dans realm_access !");
            return Collections.emptyList();
        }
        List<String> roles = (List<String>) realmAccess.get("roles");
        System.out.println("==> Roles trouves : " + roles);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }
}