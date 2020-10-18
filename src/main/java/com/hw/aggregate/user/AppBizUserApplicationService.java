package com.hw.aggregate.user;

import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.representation.AppBizUserCardRep;
import com.hw.aggregate.user.representation.AppBizUserRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.hw.shared.sql.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;

@Service
@Slf4j
public class AppBizUserApplicationService extends DefaultRoleBasedRestfulService<BizUser, AppBizUserCardRep, AppBizUserRep, VoidTypedClass> implements UserDetailsService {

    @PostConstruct
    private void setUp() {
        entityClass = BizUser.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
    }

    @Override
    public BizUser replaceEntity(BizUser bizUser, Object command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AppBizUserCardRep getEntitySumRepresentation(BizUser bizUser) {
        return new AppBizUserCardRep(bizUser);
    }

    @Override
    public AppBizUserRep getEntityRepresentation(BizUser bizUser) {
        return new AppBizUserRep(bizUser);
    }

    @Override
    protected BizUser createEntity(long id, Object command) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void preDelete(BizUser bizUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postDelete(BizUser bizUser) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void prePatch(BizUser bizUser, Map<String, Object> params, VoidTypedClass middleLayer) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void postPatch(BizUser bizUser, Map<String, Object> params, VoidTypedClass middleLayer) {
        throw new UnsupportedOperationException();
    }


    @Override
    public UserDetails loadUserByUsername(String usernameOrId) {
        try {
            return readById(Long.parseLong(usernameOrId));
        } catch (NumberFormatException ex) {
            SumPagedRep<AppBizUserCardRep> appBizUserCardRepSumPagedRep = readByQuery("email:" + usernameOrId, null, null);
            if (appBizUserCardRepSumPagedRep.getData().isEmpty())
                return null;
            return readById(appBizUserCardRepSumPagedRep.getData().get(0).getId());
        }
    }
}
