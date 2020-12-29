package com.mt.identityaccess.application.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppForgetUserPasswordCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String email;
}
