package com.mt.identityaccess.application.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppForgetBizUserPasswordCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String email;
}
