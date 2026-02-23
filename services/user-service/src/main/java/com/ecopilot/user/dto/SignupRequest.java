package com.ecopilot.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank
    private String nomUtilisateur;
    
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    private String titrePoste;
    
    @jakarta.validation.constraints.NotNull
    private String role;

    @NotBlank
    private String motDePasse;
}
