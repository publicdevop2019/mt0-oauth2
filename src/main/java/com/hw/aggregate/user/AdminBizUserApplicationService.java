package com.hw.aggregate.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.pending_user.AppPendingUserApplicationService;
import com.hw.aggregate.user.command.AdminUpdateBizUserCommand;
import com.hw.aggregate.user.model.AdminBizUserPatchMiddleLayer;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserQueryRegistry;
import com.hw.aggregate.user.representation.AdminBizUserCardRep;
import com.hw.aggregate.user.representation.AdminBizUserRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.AppChangeRecordApplicationService;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

import static com.hw.shared.AppConstant.HTTP_HEADER_AUTHORIZATION;

@Service
@Slf4j
public class AdminBizUserApplicationService extends DefaultRoleBasedRestfulService<BizUser, AdminBizUserCardRep, AdminBizUserRep, AdminBizUserPatchMiddleLayer> {
    @Autowired
    private BizUserRepo resourceOwnerRepo;

    @Autowired
    private RevokeBizUserTokenService tokenRevocationService;

    @Autowired
    private BizUserQueryRegistry bizUserQueryRegistry;
    @Autowired
    private IdGenerator idGenerator2;
    @Autowired
    private AppChangeRecordApplicationService changeRepository2;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AppPendingUserApplicationService pendingUserApplicationService;
    @Autowired
    private AppBizUserApplicationService bizUserApplicationService;


    @PostConstruct
    private void setUp() {
        repo = resourceOwnerRepo;
        queryRegistry = bizUserQueryRegistry;
        entityClass = BizUser.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        idGenerator = idGenerator2;
        appChangeRecordApplicationService = changeRepository2;
        om = objectMapper;
        entityPatchSupplier = AdminBizUserPatchMiddleLayer::new;
        deleteHook = true;
    }

    @Override
    public BizUser replaceEntity(BizUser storedBizUser, Object command) {
        return storedBizUser.replace((AdminUpdateBizUserCommand) command, tokenRevocationService);
    }

    @Override
    public AdminBizUserCardRep getEntitySumRepresentation(BizUser bizUser) {
        return new AdminBizUserCardRep(bizUser);
    }

    @Override
    public AdminBizUserRep getEntityRepresentation(BizUser bizUser) {
        return new AdminBizUserRep(bizUser);
    }

    @Override
    protected BizUser createEntity(long id, Object command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void preDelete(BizUser bizUser) {
        bizUser.validateBeforeDelete();
    }

    @Override
    public void postDelete(BizUser bizUser) {
        tokenRevocationService.blacklist(bizUser.getId());
    }

    @Override
    protected void prePatch(BizUser bizUser, Map<String, Object> params, AdminBizUserPatchMiddleLayer middleLayer) {
        AdminUpdateBizUserCommand adminUpdateBizUserCommand = new AdminUpdateBizUserCommand();
        adminUpdateBizUserCommand.setAuthorization((String) params.get(HTTP_HEADER_AUTHORIZATION));
        BeanUtils.copyProperties(bizUser, adminUpdateBizUserCommand);
        BeanUtils.copyProperties(middleLayer, adminUpdateBizUserCommand);
        bizUser.validateBeforeUpdate(adminUpdateBizUserCommand);
        bizUser.shouldRevoke(adminUpdateBizUserCommand, tokenRevocationService);//make sure validation execute before revoke

    }

    @Override
    protected void postPatch(BizUser bizUser, Map<String, Object> params, AdminBizUserPatchMiddleLayer middleLayer) {
        bizUser.validateAfterUpdate();
    }


}
