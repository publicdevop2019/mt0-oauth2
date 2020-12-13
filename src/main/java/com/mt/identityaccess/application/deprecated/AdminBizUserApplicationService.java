package com.mt.identityaccess.application.deprecated;

import com.mt.identityaccess.application.command.AdminUpdateBizUserCommand;
import com.mt.identityaccess.application.representation.AdminBizUserCardRep;
import com.mt.identityaccess.application.representation.AdminBizUserRep;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.mt.identityaccess.domain.model.user.AdminBizUserPatchMiddleLayer;
import com.mt.identityaccess.domain.model.user.User;
import com.mt.identityaccess.port.adapter.service.HttpRevokeBizUserTokenAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

import static com.hw.shared.AppConstant.HTTP_HEADER_AUTHORIZATION;

@Slf4j
@Service
public class AdminBizUserApplicationService extends RoleBasedRestfulService<User, AdminBizUserCardRep, AdminBizUserRep, AdminBizUserPatchMiddleLayer> {
    {
        entityClass = User.class;
        role = RestfulQueryRegistry.RoleEnum.ADMIN;
        entityPatchSupplier = AdminBizUserPatchMiddleLayer::new;
    }
    @Autowired
    HttpRevokeBizUserTokenAdapter tokenRevocationService;

    @Override
    public User replaceEntity(User storedBizUser, Object command) {
        return storedBizUser.replace((AdminUpdateBizUserCommand) command, tokenRevocationService);
    }

    @Override
    public AdminBizUserCardRep getEntitySumRepresentation(User bizUser) {
        return new AdminBizUserCardRep(bizUser);
    }

    @Override
    public AdminBizUserRep getEntityRepresentation(User bizUser) {
        return new AdminBizUserRep(bizUser);
    }


    @Override
    protected void preDelete(User bizUser) {
        bizUser.validateBeforeDelete();
    }

    @Override
    protected void postDelete(User bizUser) {
        tokenRevocationService.blacklist(bizUser.getId());
    }

    @Override
    protected void prePatch(User bizUser, Map<String, Object> params, AdminBizUserPatchMiddleLayer middleLayer) {
        AdminUpdateBizUserCommand adminUpdateBizUserCommand = new AdminUpdateBizUserCommand();
        adminUpdateBizUserCommand.setAuthorization((String) params.get(HTTP_HEADER_AUTHORIZATION));
        BeanUtils.copyProperties(bizUser, adminUpdateBizUserCommand);
        BeanUtils.copyProperties(middleLayer, adminUpdateBizUserCommand);
        bizUser.validateBeforeUpdate(adminUpdateBizUserCommand);
        bizUser.shouldRevoke(adminUpdateBizUserCommand, tokenRevocationService);//make sure validation execute before revoke

    }
    @Override
    protected void postPatch(User bizUser, Map<String, Object> params, AdminBizUserPatchMiddleLayer middleLayer) {
        bizUser.validateAfterUpdate();
    }


}
