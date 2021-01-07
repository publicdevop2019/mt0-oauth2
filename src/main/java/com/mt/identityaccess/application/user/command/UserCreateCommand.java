package com.mt.identityaccess.application.user.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserCreateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private Long id;

    private String email;

    private String activationCode;

    private String password;
}
