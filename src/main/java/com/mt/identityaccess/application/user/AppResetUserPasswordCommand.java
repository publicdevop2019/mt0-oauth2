package com.mt.identityaccess.application.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppResetUserPasswordCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String email;

    private String token;

    private String newPassword;
}
