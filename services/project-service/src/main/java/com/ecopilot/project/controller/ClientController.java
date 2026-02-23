package com.ecopilot.project.controller;

import com.ecopilot.project.entity.Client;
import com.ecopilot.project.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import com.ecopilot.project.dto.ApiResponse;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Client>>> getAllClients() {
        return ResponseEntity.ok(ApiResponse.<List<Client>>builder()
                .success(true)
                .data(clientService.getAllClients())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Client>> getClientById(@PathVariable Long id) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.<Client>builder()
                .success(true)
                .data(client)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Client>> createClient(@RequestPart("client") Client client, 
                               @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        // Handle files upload logic here (mocked for now)
        return ResponseEntity.ok(ApiResponse.<Client>builder()
                .success(true)
                .data(clientService.createClient(client))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Client>> updateClient(@PathVariable Long id, @RequestBody Client client) {
        Client updated = clientService.updateClient(id, client);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ApiResponse.<Client>builder()
                .success(true)
                .data(updated)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .build());
    }
}
