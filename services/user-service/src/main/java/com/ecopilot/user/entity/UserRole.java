package com.ecopilot.user.entity;

public enum UserRole {
    ASSISTANTE_ADMINISTRATIVE("Assistante administrative"),
    CHARGES_ETUDES("Chargés d’études"),
    RESPONSABLES_METRES("Responsables métrés"),
    CHEFS_PROJET("Chefs de projet"),
    RESPONSABLE_ETUDES_PRIX("Responsable études de prix"),
    FOURNISSEURS("Fournisseurs"),
    ADMINISTRATEUR_SOLUTION("Administrateur de la solution"),
    SUPER_ADMIN("Super admin"),
    ADMIN("admin"),
    USER("Utilisateur");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    @com.fasterxml.jackson.annotation.JsonValue
    public String getValue() {
        return value;
    }

    @com.fasterxml.jackson.annotation.JsonCreator
    public static UserRole fromValue(String value) {
        for (UserRole role : UserRole.values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown UserRole: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}
