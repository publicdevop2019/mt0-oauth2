package com.mt.access.domain.model.ticket;

import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.user.UserId;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TicketInfo {
    public static String USER_ID = "userId";
    public static String CLIENT_ID = "clientId";
    private Long exp;
    private UserId userId;
    private ClientId clientId;

    private TicketInfo(UserId userId, ClientId clientId) {
        this.exp = System.currentTimeMillis() + 5000L;
        this.userId = userId;
        this.clientId = clientId;
    }

    public static TicketInfo create(UserId userId, ClientId clientId) {
        return new TicketInfo(userId, clientId);
    }
}
