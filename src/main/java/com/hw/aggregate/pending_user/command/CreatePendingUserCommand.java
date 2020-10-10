package com.hw.aggregate.pending_user.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class CreatePendingUserCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String email;

}
