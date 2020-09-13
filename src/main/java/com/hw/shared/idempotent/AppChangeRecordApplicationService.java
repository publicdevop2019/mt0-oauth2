package com.hw.shared.idempotent;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.command.AppCreateChangeRecordCommand;
import com.hw.shared.idempotent.model.ChangeRecord;
import com.hw.shared.idempotent.model.ChangeRecordQueryRegistry;
import com.hw.shared.idempotent.representation.AppChangeRecordCardRep;
import com.hw.shared.rest.CreatedEntityRep;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import com.hw.shared.sql.RestfulQueryRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Service
public class AppChangeRecordApplicationService extends DefaultRoleBasedRestfulService<ChangeRecord, AppChangeRecordCardRep, Void, VoidTypedClass> {
    @Autowired
    private IdGenerator idGenerator2;
    @Autowired
    private ChangeRepository changeHistoryRepository;
    @Autowired
    private ObjectMapper om2;
    @Autowired
    private ChangeRecordQueryRegistry changeRecordQueryRegistry;
    @Autowired
    private ApplicationContext context;
    @Override
    public ChangeRecord replaceEntity(ChangeRecord changeRecord, Object command) {
        return null;
    }

    @Override
    public AppChangeRecordCardRep getEntitySumRepresentation(ChangeRecord changeRecord) {
        return new AppChangeRecordCardRep(changeRecord);
    }

    @Override
    public Void getEntityRepresentation(ChangeRecord changeRecord) {
        return null;
    }

    @Override
    protected ChangeRecord createEntity(long id, Object command) {
        return null;
    }

    @Override
    public void preDelete(ChangeRecord changeRecord) {

    }

    @Override
    public void postDelete(ChangeRecord changeRecord) {

    }

    @Override
    protected void prePatch(ChangeRecord changeRecord, Map<String, Object> params, VoidTypedClass middleLayer) {

    }

    @Override
    protected void postPatch(ChangeRecord changeRecord, Map<String, Object> params, VoidTypedClass middleLayer) {

    }

    @PostConstruct
    private void setUp() {
        repo = changeHistoryRepository;
        idGenerator = idGenerator2;
        entityClass = ChangeRecord.class;
        role = RestfulQueryRegistry.RoleEnum.APP;
        queryRegistry = changeRecordQueryRegistry;
        om = om2;
    }

    @Transactional
    public CreatedEntityRep create(AppCreateChangeRecordCommand command) {
        long id = idGenerator.getId();
        ChangeRecord changeRecord = ChangeRecord.create(id, command);
        ChangeRecord saved = repo.save(changeRecord);
        return new CreatedEntityRep(saved);
    }
    @Transactional
    public void deleteByQuery(String queryParam) {
        List<AppChangeRecordCardRep> allByQuery = getAllByQuery(queryParam);
        allByQuery.forEach(e -> {
            Class<?> aClass = null;
            try {
                aClass = Class.forName(e.getServiceBeanName());
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
            DefaultRoleBasedRestfulService bean = (DefaultRoleBasedRestfulService) context.getBean(aClass);
            bean.rollback(e.getChangeId());
        });

    }
}
