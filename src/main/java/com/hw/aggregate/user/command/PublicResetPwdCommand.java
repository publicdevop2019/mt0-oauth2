package com.hw.aggregate.user.command;

import lombok.Data;

import javax.persistence.Column;
@Data
public class PublicResetPwdCommand {
    private String email;

    private String token;

    private String newPassword;
}
