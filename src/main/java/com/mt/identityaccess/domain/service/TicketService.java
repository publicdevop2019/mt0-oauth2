package com.mt.identityaccess.domain.service;

import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.ticket.SignedTicket;
import com.mt.identityaccess.domain.model.user.UserId;

public interface TicketService {
    SignedTicket create(UserId userId, ClientId clientId);
}
