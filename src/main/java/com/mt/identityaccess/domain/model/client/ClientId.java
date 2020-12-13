package com.mt.identityaccess.domain.model.client;

public class ClientId {
    private String id;
    private Long dbId;

    public ClientId(String id) {
        this.id=id;
        this.dbId=parseFrom(id);
    }

    private Long parseFrom(String id) {
        return null;
    }

    public String id() {
        return id;
    }
    public Long persistentId(){
        return dbId;
    }
}
