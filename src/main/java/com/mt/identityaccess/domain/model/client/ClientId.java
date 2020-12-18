package com.mt.identityaccess.domain.model.client;

public class ClientId {
    private String clientId;

    public ClientId(String id) {
        this.clientId = id;
    }

    public ClientId() {
    }

    public ClientId(long id) {
        this.clientId = String.valueOf(id);
    }

    public String id() {
        return clientId;
    }
}
