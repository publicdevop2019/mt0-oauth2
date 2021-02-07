package com.mt.identityaccess.application.ticket;

import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.ticket.SignedTicket;
import com.mt.identityaccess.domain.model.user.UserId;
import org.springframework.stereotype.Service;

@Service
public class TicketApplicationService {
    public SignedTicket create() {
        UserId userId = DomainRegistry.authenticationService().getUserId();
        ClientId clientId = DomainRegistry.authenticationService().getClientId();
        return DomainRegistry.ticketService().create(userId, clientId);
    }
}
