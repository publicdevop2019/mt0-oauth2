package com.hw.aggregate.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.client.command.CreateClientCommand;
import com.hw.aggregate.client.command.UpdateClientCommand;
import com.hw.aggregate.client.model.BizClient;
import com.hw.aggregate.client.model.BizClientQueryRegistry;
import com.hw.aggregate.client.model.RootBizClientPatchMiddleLayer;
import com.hw.aggregate.client.representation.RootBizClientCardRep;
import com.hw.aggregate.client.representation.RootBizClientRep;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.AppChangeRecordApplicationService;
import com.hw.shared.idempotent.ChangeRepository;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@SuppressWarnings("unused")
@Service
@Slf4j
public class RootBIzClientApplicationService extends DefaultRoleBasedRestfulService<BizClient, RootBizClientCardRep, RootBizClientRep, RootBizClientPatchMiddleLayer> {

    @Autowired
    BizClientRepo clientRepo;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    RevokeBizClientTokenService tokenRevocationService;
    @Autowired
    AppBizClientApplicationService appBizClientApplicationService;

    @Autowired
    private BizClientQueryRegistry clientQueryRegistry;

    @Autowired
    private IdGenerator idGenerator2;
    @Autowired
    private AppChangeRecordApplicationService changeHistoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    private void setUp() {
        repo = clientRepo;
        queryRegistry = clientQueryRegistry;
        entityClass = BizClient.class;
        role = RestfulQueryRegistry.RoleEnum.ROOT;
        idGenerator = idGenerator2;
        appChangeRecordApplicationService = changeHistoryRepository;
        entityPatchSupplier = RootBizClientPatchMiddleLayer::new;
        om = objectMapper;
        deleteHook = true;
    }

    @Override
    public BizClient replaceEntity(BizClient stored, Object command) {
        return stored.replace((UpdateClientCommand) command, tokenRevocationService, appBizClientApplicationService, encoder);
    }

    @Override
    public void preDelete(BizClient bizClient) {
        bizClient.validateDelete();
    }

    @Override
    public void postDelete(BizClient bizClient) {
        tokenRevocationService.blacklist(bizClient.getId());
    }

    @Override
    protected void prePatch(BizClient bizClient, Map<String, Object> params, RootBizClientPatchMiddleLayer middleLayer) {
        UpdateClientCommand updateClientCommand = new UpdateClientCommand();
        BeanUtils.copyProperties(bizClient, updateClientCommand);//copy old values so shouldRevoke will work
        BeanUtils.copyProperties(middleLayer, updateClientCommand);
        BizClient.validateResourceId(bizClient, appBizClientApplicationService);
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
        return BizClient.create(id, (CreateClientCommand) command, appBizClientApplicationService, encoder);
    }

}
