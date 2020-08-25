package com.hw.aggregate.user.command;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.Email;
@Data
public class CreateBizUserCommand {
    private Long id;

    private String email;

    private String activationCode;

    private String password;
}
