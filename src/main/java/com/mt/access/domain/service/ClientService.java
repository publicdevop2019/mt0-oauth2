package com.mt.access.domain.service;

import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.*;
import com.mt.access.domain.model.client.event.ClientCreated;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ClientService {

    public ClientId create(ClientId clientId,
                           String name,
                           String clientSecret,
                           String description,
                           boolean accessible,
                           Set<Scope> scopes,
                           Set<Role> authorities,
                           Set<ClientId> resources,
                           ClientCredentialsGrant clientCredentialsGrant,
                           PasswordGrant passwordGrant,
                           AuthorizationCodeGrant authorizationCodeGrant
    ) {

        Client client = new Client(
                clientId,
                name,
                clientSecret,
                description,
                accessible,
                scopes,
                authorities,
                resources,
                clientCredentialsGrant,
                passwordGrant,
                authorizationCodeGrant
        );
        DomainRegistry.getClientRepository().add(client);
        DomainEventPublisher.instance().publish(new ClientCreated(client.getClientId()));
        return clientId;
    }
}
