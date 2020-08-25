package com.hw.aggregate.user.command;

import lombok.Data;

@Data
public class PublicForgetPasswordCommand {
    private String email;
}
