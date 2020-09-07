package com.hw.shared.idempotent;

import com.hw.shared.idempotent.model.ChangeRecord;
import com.hw.shared.rest.DefaultRoleBasedRestfulService;
import com.hw.shared.rest.VoidTypedClass;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AppChangeRecordApplicationService extends DefaultRoleBasedRestfulService<ChangeRecord, Void, Void, VoidTypedClass> {
    @Override
    public ChangeRecord replaceEntity(ChangeRecord changeRecord, Object command) {
        return null;
    }

    @Override
    public Void getEntitySumRepresentation(ChangeRecord changeRecord) {
        return null;
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
}
