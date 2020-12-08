package com.hw.aggregate.client;

import com.hw.aggregate.client.command.RootCreateBizClientCommand;
import com.hw.aggregate.client.command.RootUpdateBizClientCommand;
import com.hw.aggregate.client.model.BizClient;
import com.hw.aggregate.client.model.RootBizClientPatchMiddleLayer;
import com.hw.aggregate.client.representation.RootBizClientCardRep;
import com.hw.aggregate.client.representation.RootBizClientRep;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@SuppressWarnings("unused")
@Service
@Slf4j
public class RootBizClientApplicationService extends RoleBasedRestfulService<BizClient, RootBizClientCardRep, RootBizClientRep, RootBizClientPatchMiddleLayer> {
    {
        entityClass = BizClient.class;
        role = RestfulQueryRegistry.RoleEnum.ROOT;
        entityPatchSupplier = RootBizClientPatchMiddleLayer::new;
    }

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    RevokeBizClientTokenService tokenRevocationService;
    @Autowired
    AppBizClientApplicationService appBizClientApplicationService;

    @Override
    public BizClient replaceEntity(BizClient stored, Object command) {
        return stored.replace((RootUpdateBizClientCommand) command, tokenRevocationService, encoder,repo);
    }

    @Override
    protected void preDelete(BizClient bizClient) {
        bizClient.validateDelete();
    }

    @Override
    protected void postDelete(BizClient bizClient) {
        tokenRevocationService.blacklist(bizClient.getId());
    }

    @Override
    protected void prePatch(BizClient bizClient, Map<String, Object> params, RootBizClientPatchMiddleLayer middleLayer) {
        RootUpdateBizClientCommand updateClientCommand = new RootUpdateBizClientCommand();
        BeanUtils.copyProperties(bizClient, updateClientCommand);//copy old values so shouldRevoke will work
        BeanUtils.copyProperties(middleLayer, updateClientCommand);
        BizClient.validateResourceId(bizClient);
        bizClient.shouldRevoke(updateClientCommand, tokenRevocationService);//make sure validation execute before revoke
    }

    @Override
    protected void postPatch(BizClient bizClient, Map<String, Object> params, RootBizClientPatchMiddleLayer middleLayer) {
        BizClient.validateResourceIndicator(bizClient);
    }

    @Override
    public RootBizClientCardRep getEntitySumRepresentation(BizClient client) {
        return new RootBizClientCardRep(client);
    }

    @Override
    public RootBizClientRep getEntityRepresentation(BizClient client) {
        return new RootBizClientRep(client);
    }

    @Override
    protected BizClient createEntity(long id, Object command) {
        return BizClient.create(id, (RootCreateBizClientCommand) command, appBizClientApplicationService, encoder, repo);
    }
}
