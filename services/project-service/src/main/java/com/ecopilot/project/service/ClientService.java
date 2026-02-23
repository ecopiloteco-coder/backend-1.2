package com.ecopilot.project.service;

import com.ecopilot.project.entity.Client;
import com.ecopilot.project.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    public Client updateClient(Long id, Client clientDetails) {
        return clientRepository.findById(id).map(client -> {
            if (clientDetails.getNomClient() != null) {
                client.setNomClient(clientDetails.getNomClient());
            }
            if (clientDetails.getMargeBrut() != null) {
                client.setMargeBrut(clientDetails.getMargeBrut());
            }
            if (clientDetails.getMargeNet() != null) {
                client.setMargeNet(clientDetails.getMargeNet());
            }
            if (clientDetails.getAgence() != null) {
                client.setAgence(clientDetails.getAgence());
            }
            if (clientDetails.getResponsable() != null) {
                client.setResponsable(clientDetails.getResponsable());
            }
            if (clientDetails.getEffectifChantier() != null) {
                client.setEffectifChantier(clientDetails.getEffectifChantier());
            }
            return clientRepository.save(client);
        }).orElse(null);
    }

    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }
}
