package com.mt.identityaccess.application.representation;

import com.mt.identityaccess.domain.model.pending_user.PendingUser;
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
