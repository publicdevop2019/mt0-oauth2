package com.mt.identityaccess.domain.service;

import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.client.ClientQuery;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ClientValidationService {
    public void validate(Client client, ValidationNotificationHandler handler) {
        if (!client.getResources().isEmpty()) {
            Set<Client> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.clientRepository().clientsOfQuery((ClientQuery) query), new ClientQuery(client.getResources()));
            if (allByQuery.size() != client.getResources().size()) {
                handler.handleError("unable to find all resource(s)");
            }
            boolean b = allByQuery.stream().anyMatch(e -> !e.isAccessible());
            if (b) {
                handler.handleError("invalid resource(s) found");
            }
        }
    }
}
