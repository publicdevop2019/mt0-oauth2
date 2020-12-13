package com.mt.identityaccess.domain.model;

import com.mt.identityaccess.domain.model.client.BizClientRepository;
import com.mt.identityaccess.domain.model.pending_user.PendingUserRepository;
import com.mt.identityaccess.domain.model.user.BizUserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class DomainRegistry {
    @Autowired
    private static BizClientRepository bizClientRepository;
    @Autowired
    private static BizUserRepository bizUserRepo;
    @Autowired
    private static PendingUserRepository pendingUserRepo;

    public static BizClientRepository bizClientRepository(){
        return bizClientRepository;
    }
}
