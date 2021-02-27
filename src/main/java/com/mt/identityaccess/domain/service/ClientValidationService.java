package com.mt.identityaccess.domain.service;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.identityaccess.domain.model.client.ClientQuery;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.Client;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ClientValidationService {
    public void validate(Client client, ValidationNotificationHandler handler) {
        if (!client.getResources().isEmpty()) {
            Set<Client> clientsOfQuery = DomainRegistry.clientService().getClientsOfQuery(new ClientQuery(client.getResources()));
            if (clientsOfQuery.size() != client.getResources().size()) {
                handler.handleError("unable to find all resource(s)");
            }
            boolean b = clientsOfQuery.stream().anyMatch(e -> !e.isAccessible());
            if (b) {
                handler.handleError("invalid resource(s) found");
            }
        }
    }
}
