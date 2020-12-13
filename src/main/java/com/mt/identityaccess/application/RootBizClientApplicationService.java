package com.mt.identityaccess.application;

import com.mt.identityaccess.application.command.ProvisionClientCommand;
import com.mt.identityaccess.application.command.ReplaceClientCommand;
import com.mt.identityaccess.application.representation.RootClientCardRepresentation;
import com.mt.identityaccess.application.representation.RootClientRepresentation;
import com.hw.shared.rest.RoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.port.adapter.service.HttpRevokeBizClientTokenAdapter;
import com.mt.identityaccess.domain.model.client.ClientPatchingMiddleLayer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@SuppressWarnings("unused")
@Service
@Slf4j
public class RootBizClientApplicationService extends RoleBasedRestfulService<Client, RootClientCardRepresentation, RootClientRepresentation, ClientPatchingMiddleLayer> {
    {
        entityClass = Client.class;
        role = RestfulQueryRegistry.RoleEnum.ROOT;
        entityPatchSupplier = ClientPatchingMiddleLayer::new;
    }

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    HttpRevokeBizClientTokenAdapter tokenRevocationService;
    @Autowired
    AppBizClientApplicationService appBizClientApplicationService;

    @Override
    public Client replaceEntity(Client stored, Object command) {
        return stored.replace((ReplaceClientCommand) command, tokenRevocationService, encoder,repo);
    }

    @Override
    protected void preDelete(Client bizClient) {
        bizClient.validateDelete();
    }

    @Override
    protected void postDelete(Client bizClient) {
        tokenRevocationService.blacklist(bizClient.getId());
    }

    @Override
    protected void prePatch(Client bizClient, Map<String, Object> params, ClientPatchingMiddleLayer middleLayer) {
        ReplaceClientCommand updateClientCommand = new ReplaceClientCommand();
        BeanUtils.copyProperties(bizClient, updateClientCommand);//copy old values so shouldRevoke will work
        BeanUtils.copyProperties(middleLayer, updateClientCommand);
        Client.validateResourceId(bizClient);
        bizClient.shouldRevoke(updateClientCommand, tokenRevocationService);//make sure validation execute before revoke
    }

    @Override
    protected void postPatch(Client bizClient, Map<String, Object> params, ClientPatchingMiddleLayer middleLayer) {
        Client.validateResourceIndicator(bizClient);
    }

    @Override
    public RootClientCardRepresentation getEntitySumRepresentation(Client client) {
        return new RootClientCardRepresentation(client);
    }

    @Override
    public RootClientRepresentation getEntityRepresentation(Client client) {
        return new RootClientRepresentation(client);
    }

    @Override
    protected Client createEntity(long id, Object command) {
        return Client.create(id, (ProvisionClientCommand) command, appBizClientApplicationService, encoder, repo);
    }
}
