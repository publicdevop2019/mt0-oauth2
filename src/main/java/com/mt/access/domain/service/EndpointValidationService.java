package com.mt.access.domain.service;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.endpoint.Endpoint;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EndpointValidationService {
    public void validate(Endpoint endpoint, ValidationNotificationHandler handler) {
        ClientId clientId = endpoint.getClientId();
        Optional<Client> client = DomainRegistry.clientRepository().clientOfId(clientId);
        if (client.isEmpty()) {
            handler.handleError("can not update endpoint it which clientId is deleted or unknown");
        }
    }
}
