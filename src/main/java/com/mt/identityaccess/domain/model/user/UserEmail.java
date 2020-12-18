package com.mt.identityaccess.domain.model.user;

public class UserEmail {
    private String email;
    public String plainValue() {
        return email;
    }

    public UserEmail(String email) {
        this.email = email;
    }
}
