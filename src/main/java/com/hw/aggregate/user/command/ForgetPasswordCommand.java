package com.hw.aggregate.user.command;

import lombok.Data;

@Data
public class ForgetPasswordCommand {
    private String email;
}
