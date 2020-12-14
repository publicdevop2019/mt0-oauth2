package com.hw.application.representation;

import com.hw.domain.model.pending_user.PendingUser;
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
