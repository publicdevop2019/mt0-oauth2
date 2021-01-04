package com.mt.identityaccess.application.pending_user;

import lombok.Data;

import java.io.Serializable;

@Data
public class PendingUserCreateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String email;

}
