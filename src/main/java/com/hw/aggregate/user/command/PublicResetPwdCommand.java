package com.hw.aggregate.user.command;

import lombok.Data;

@Data
public class PublicResetPwdCommand {
    private String email;

    private String token;

    private String newPassword;
}
