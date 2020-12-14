package com.hw.application.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class AppCreatePendingUserCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String email;

}
