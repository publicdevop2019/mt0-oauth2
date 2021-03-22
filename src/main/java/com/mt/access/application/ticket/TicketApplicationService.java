package com.mt.access.application.ticket;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.ticket.SignedTicket;
import com.mt.access.domain.model.user.UserId;
import org.springframework.stereotype.Service;

@Service
public class TicketApplicationService {
    public SignedTicket create() {
        UserId userId = DomainRegistry.authenticationService().getUserId();
        ClientId clientId = DomainRegistry.authenticationService().getClientId();
        return DomainRegistry.ticketService().create(userId, clientId);
    }
}
