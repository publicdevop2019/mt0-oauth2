package com.hw.aggregate.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.aggregate.pending_user.AppPendingUserApplicationService;
import com.hw.aggregate.pending_user.PendingUserRepo;
import com.hw.aggregate.user.command.UpdateBizUserCommand;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserQueryRegistry;
import com.hw.aggregate.user.representation.AdminBizUserCardRep;
import com.hw.aggregate.user.representation.AdminBizUserRep;
import com.hw.shared.BadRequestException;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.ChangeRepository;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
@Slf4j
public class AdminBizUserApplicationService extends DefaultRoleBasedRestfulService<BizUser, AdminBizUserCardRep, AdminBizUserRep, VoidTypedClass> {
    @Autowired
    private BizUserRepo resourceOwnerRepo;

    @Autowired
    private RevokeBizUserTokenService tokenRevocationService;

    @Autowired
    private BizUserQueryRegistry bizUserQueryRegistry;
    @Autowired
    private IdGenerator idGenerator2;
    @Autowired
    private ChangeRepository changeRepository2;
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
        changeRepository = changeRepository2;
        om = objectMapper;
    }

    @Override
    public Integer deleteById(Long id) {
        AdminBizUserRep adminBizUserRep = readById(id);
        if (adminBizUserRep.getGrantedAuthorities().stream().anyMatch(e -> "ROLE_ROOT".equals(e.getAuthority())))
            throw new BadRequestException("root account can not be modified");
        tokenRevocationService.blacklist(adminBizUserRep.getId().toString(), true);
        return super.deleteById(id);
    }

    @Override
    public BizUser replaceEntity(BizUser storedBizUser, Object command) {
        return storedBizUser.replace((UpdateBizUserCommand) command, tokenRevocationService);
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
        return null;
    }
}
