package com.hw.aggregate.pending_user.representation;

import com.hw.aggregate.pending_user.model.PendingUser;
import lombok.Data;

import javax.persistence.Column;
@Data
public class AppPendingUserCardRep {
    private String email;

    private String activationCode;
    public AppPendingUserCardRep(PendingUser pendingBizUser) {
        this.email = pendingBizUser.getEmail();
        this.activationCode = pendingBizUser.getActivationCode();

    }

}
