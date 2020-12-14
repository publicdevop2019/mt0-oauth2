package com.mt.identityaccess.domain.model.client;

public class ClientId {
    private String id;
    private Long dbId;

    public ClientId(String id) {
        this.id = id;
        this.dbId = parseFrom(id);
    }

    public ClientId() {
    }

    public ClientId(long id) {
        this.id = String.valueOf(id);
        this.dbId = id;
    }

    private Long parseFrom(String id) {
        return Long.parseLong(id);
    }

    public String id() {
        return id;
    }

    public Long persistentId() {
        return dbId;
    }
}
