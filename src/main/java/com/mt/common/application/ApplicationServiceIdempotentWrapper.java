package com.mt.common.application;

import com.mt.common.domain.model.id.DomainId;
import com.mt.common.idempotent.AppChangeRecordApplicationService;
import com.mt.common.idempotent.OperationType;
import com.mt.common.idempotent.command.AppCreateChangeRecordCommand;
import com.mt.common.idempotent.representation.AppChangeRecordCardRep;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.client.ClientId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.mt.common.AppConstant.CHANGE_REVOKED;
import static com.mt.common.idempotent.model.ChangeRecord.CHANGE_ID;
import static com.mt.common.idempotent.model.ChangeRecord.ENTITY_TYPE;

@Service
public class ApplicationServiceIdempotentWrapper {
    @Autowired
    AppChangeRecordApplicationService appChangeRecordApplicationService;

    public DomainId idempotentCreate(Object command, String changeId, DomainId domainId, Supplier<DomainId> wrapper) {
        String entityType = getEntityName();
        if (changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
            SumPagedRep<AppChangeRecordCardRep> appChangeRecordCardRepSumPagedRep = appChangeRecordApplicationService.readByQuery(CHANGE_ID + ":" + changeId + "," + ENTITY_TYPE + ":" + entityType, null, "sc:1");
            return new ClientId(appChangeRecordCardRepSumPagedRep.getData().get(0).getQuery().replace("id:", ""));
        } else if (changeAlreadyExist(changeId) && !changeAlreadyRevoked(changeId)) {
            SumPagedRep<AppChangeRecordCardRep> appChangeRecordCardRepSumPagedRep = appChangeRecordApplicationService.readByQuery(CHANGE_ID + ":" + changeId + "," + ENTITY_TYPE + ":" + entityType, null, "sc:1");
            return new ClientId(appChangeRecordCardRepSumPagedRep.getData().get(0).getQuery().replace("id:", ""));
        } else if (!changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
            return new ClientId();
        } else {
            saveChangeRecord(command, changeId, OperationType.POST, "id:" + domainId.getDomainId(), null, null);
            return wrapper.get();
        }
    }

    public void idempotent(Object command, String changeId, Consumer<String> wrapper) {
        if (!changeAlreadyExist(changeId) && !changeAlreadyRevoked(changeId)) {
            saveChangeRecord(command, changeId, OperationType.PUT, "id:", null, null);
            wrapper.accept(null);
        }
    }

    protected boolean changeAlreadyRevoked(String changeId) {
        String entityType = getEntityName();
        SumPagedRep<AppChangeRecordCardRep> appChangeRecordCardRepSumPagedRep = appChangeRecordApplicationService.readByQuery(CHANGE_ID + ":" + changeId + CHANGE_REVOKED + "," + ENTITY_TYPE + ":" + entityType, null, "sc:1");
        return (appChangeRecordCardRepSumPagedRep.getData() != null && appChangeRecordCardRepSumPagedRep.getData().size() > 0);
    }

    protected boolean changeAlreadyExist(String changeId) {
        String entityType = getEntityName();
        SumPagedRep<AppChangeRecordCardRep> appChangeRecordCardRepSumPagedRep = appChangeRecordApplicationService.readByQuery(CHANGE_ID + ":" + changeId + "," + ENTITY_TYPE + ":" + entityType, null, "sc:1");
        return (appChangeRecordCardRepSumPagedRep.getData() != null && appChangeRecordCardRepSumPagedRep.getData().size() > 0);
    }

    protected String getEntityName() {
        String[] split = Client.class.getName().split("\\.");
        return split[split.length - 1];
    }

    protected void saveChangeRecord(Object requestBody, String changeId, OperationType operationType, String query, Set<Long> deletedIds, Object toBeReplaced) {
        AppCreateChangeRecordCommand changeRecord = new AppCreateChangeRecordCommand();
        changeRecord.setChangeId(changeId);
        changeRecord.setEntityType(getEntityName());
        changeRecord.setServiceBeanName(this.getClass().getName());
        changeRecord.setOperationType(operationType);
        changeRecord.setQuery(query);
        changeRecord.setReplacedVersion(toBeReplaced);
        changeRecord.setDeletedIds(deletedIds);
        changeRecord.setRequestBody(requestBody);
        appChangeRecordApplicationService.create(changeRecord);
    }
}