package com.hw.aggregate.user.command;

import lombok.Data;

@Data
public class PublicCreateBizUserCommand {
    private Long id;

    private String email;

    private String activationCode;

    private String password;
}
