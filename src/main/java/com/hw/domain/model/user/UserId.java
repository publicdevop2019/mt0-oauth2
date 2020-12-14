package com.hw.domain.model.user;

public class UserId {
    private String id;
    private Long dbId;
    public String id() {
        return id;
    }
    public Long persistentId(){
        return dbId;
    }
}
