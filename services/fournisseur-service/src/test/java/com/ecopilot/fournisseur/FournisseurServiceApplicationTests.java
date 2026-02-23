package com.ecopilot.fournisseur;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.keycloak.admin.client.Keycloak;

@SpringBootTest
@ActiveProfiles("test")
class FournisseurServiceApplicationTests {

    @MockitoBean
    private Keycloak keycloak;

    @Test
    void contextLoads() {
    }
}
