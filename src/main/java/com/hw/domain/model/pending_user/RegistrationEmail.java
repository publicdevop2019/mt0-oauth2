package com.hw.domain.model.pending_user;

public class RegistrationEmail {
    private String email;

    public RegistrationEmail(String email) {
        //validate email format
        this.email = email;
    }

    public String plainValue() {
        return email;
    }
}
