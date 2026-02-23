package com.ecopilot.fournisseur;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FournisseurServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FournisseurServiceApplication.class, args);
    }
}
