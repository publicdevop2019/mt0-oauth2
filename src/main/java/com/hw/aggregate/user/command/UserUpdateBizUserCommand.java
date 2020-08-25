package com.hw.aggregate.user.command;

import lombok.Data;

@Data
public class UserUpdateBizUserCommand {
    private String currentPwd;
    private String password;
}
