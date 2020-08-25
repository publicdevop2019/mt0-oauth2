package com.hw.aggregate.pending_user;

import com.hw.aggregate.pending_user.model.PendingBizUser;
import com.hw.aggregate.user.BizUserApplicationService;
import com.hw.shared.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PublicPendingBizUserApplicationService {
    @Autowired
    private ActivationCodeEmailService activationCodeEmailService;
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    private PendingBizUserRepo pendingBizUserRepo;
    @Autowired
    private BizUserApplicationService bizUserApplicationService;

    public void createPendingResourceOwner(PendingBizUser pendingResourceOwner) {
        PendingBizUser pendingResourceOwner1 = PendingBizUser.create(pendingResourceOwner.getEmail(), pendingBizUserRepo, bizUserApplicationService, idGenerator);
        activationCodeEmailService.sendActivationCode(pendingResourceOwner1.getActivationCode(), pendingResourceOwner1.getEmail());
    }
}
