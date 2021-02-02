package com.mt.identityaccess.domain.service;

import com.mt.common.validate.ValidationNotificationHandler;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.endpoint.Endpoint;
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
