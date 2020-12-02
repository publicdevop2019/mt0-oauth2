package com.hw.aggregate.pending_user.representation;

import com.hw.aggregate.pending_user.model.PendingUser;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class AppPendingUserCardRep {
    private String email;

    private String activationCode;

    public AppPendingUserCardRep(PendingUser pendingBizUser) {
        BeanUtils.copyProperties(pendingBizUser, this);

    }

}
