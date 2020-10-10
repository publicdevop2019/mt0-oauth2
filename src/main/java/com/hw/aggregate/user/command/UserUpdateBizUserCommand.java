package com.hw.aggregate.user.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateBizUserCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String currentPwd;
    private String password;
}
