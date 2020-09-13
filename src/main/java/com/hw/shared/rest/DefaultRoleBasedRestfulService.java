package com.hw.shared.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hw.shared.Auditable;
import com.hw.shared.AuditorAwareImpl;
import com.hw.shared.DeepCopyException;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.AppChangeRecordApplicationService;
import com.hw.shared.idempotent.OperationType;
import com.hw.shared.idempotent.command.AppCreateChangeRecordCommand;
import com.hw.shared.idempotent.exception.ChangeNotFoundException;
import com.hw.shared.idempotent.exception.HangingTransactionException;
import com.hw.shared.idempotent.exception.RollbackNotSupportedException;
import com.hw.shared.idempotent.representation.AppChangeRecordCardRep;
import com.hw.shared.rest.exception.EntityNotExistException;
import com.hw.shared.rest.exception.EntityPatchException;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.hw.shared.sql.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hw.shared.AppConstant.*;
import static com.hw.shared.idempotent.model.ChangeRecord.CHANGE_ID;
import static com.hw.shared.idempotent.model.ChangeRecord.ENTITY_TYPE;

@Slf4j
public abstract class DefaultRoleBasedRestfulService<T extends Auditable & IdBasedEntity, X, Y, Z extends TypedClass<Z>> {

    protected JpaRepository<T, Long> repo;
    protected IdGenerator idGenerator;
    protected RestfulQueryRegistry<T> queryRegistry;

    protected Class<T> entityClass;

    protected Function<T, Z> entityPatchSupplier;

    protected RestfulQueryRegistry.RoleEnum role;
    protected ObjectMapper om;
    protected AppChangeRecordApplicationService appChangeRecordApplicationService;
    protected boolean deleteHook = false;

    @Transactional
    public CreatedEntityRep create(Object command, String changeId) {
        long id = idGenerator.getId();
        saveChangeRecord(null, changeId, OperationType.POST, "id:" + id);
        T created = createEntity(id, command);
        T save = repo.save(created);
        return getCreatedEntityRepresentation(save);
    }

    @Transactional
    public void replaceById(Long id, Object command, String changeId) {
        saveChangeRecord(null, changeId, OperationType.PUT, "id:" + id.toString());
        SumPagedRep<T> tSumPagedRep = getEntityById(id);
        T after = replaceEntity(tSumPagedRep.getData().get(0), command);
        repo.save(after);
    }


    @Transactional
    public void patchById(Long id, JsonPatch patch, Map<String, Object> params) {
        saveChangeRecord(null, (String) params.get(HTTP_HEADER_CHANGE_ID), OperationType.PATCH_BY_ID, "id:" + id.toString());
        SumPagedRep<T> entityById = getEntityById(id);
        T original = entityById.getData().get(0);
        Z command = entityPatchSupplier.apply(original);
        try {
            JsonNode jsonNode = om.convertValue(command, JsonNode.class);
            JsonNode patchedNode = patch.apply(jsonNode);
            command = om.treeToValue(patchedNode, command.getClazz());
        } catch (JsonPatchException | JsonProcessingException e) {
            e.printStackTrace();
            throw new EntityPatchException();
        }
        prePatch(original, params, command);
        BeanUtils.copyProperties(command, original);
        repo.save(original);
        postPatch(original, params, command);
    }

    @Transactional
    public Integer patchBatch(List<PatchCommand> commands, String changeId) {
        saveChangeRecord(commands, changeId, OperationType.PATCH_BATCH, null);
        List<PatchCommand> deepCopy = getDeepCopy(commands);
        return queryRegistry.update(role, deepCopy, entityClass);
    }

    @Transactional
    public Integer deleteById(Long id, String changeId) {
        saveChangeRecord(null, changeId, OperationType.DELETE_BY_ID, "id:" + id.toString());
        return doDelete("id:" + id);
    }

    @Transactional
    public Integer deleteByQuery(String query, String changeId) {
        saveChangeRecord(null, changeId, OperationType.DELETE_BY_QUERY, query);
        return doDelete(query);

    }

    private Integer doDelete(String query) {
        if (deleteHook) {
            int pageNum = 0;
            SumPagedRep<T> tSumPagedRep = queryRegistry.readByQuery(role, query, "num:" + pageNum, null, entityClass);
            long l = tSumPagedRep.getTotalItemCount() / tSumPagedRep.getData().size();
            double ceil = Math.ceil(l);
            int i = BigDecimal.valueOf(ceil).intValue();
            List<T> data = new ArrayList<>(tSumPagedRep.getData());
            for (int a = 1; a < i; a++) {
                data = queryRegistry.readByQuery(role, query, "num:" + a, null, entityClass).getData();
            }
            data.forEach(this::preDelete);
            Set<String> collect = data.stream().map(e -> e.getId().toString()).collect(Collectors.toSet());
            String join = "id:" + String.join(".", collect);
            queryRegistry.deleteByQuery(role, join, entityClass);//delete only checked entity
            data.forEach(this::postDelete);
            return data.size();
        } else {
            return queryRegistry.deleteByQuery(role, query, entityClass);
        }
    }

    @Transactional(readOnly = true)
    public SumPagedRep<X> readByQuery(String query, String page, String config) {
        SumPagedRep<T> tSumPagedRep = queryRegistry.readByQuery(role, query, page, config, entityClass);
        List<X> col = tSumPagedRep.getData().stream().map(this::getEntitySumRepresentation).collect(Collectors.toList());
        return new SumPagedRep<>(col, tSumPagedRep.getTotalItemCount());
    }


    @Transactional(readOnly = true)
    public Y readById(Long id) {
        SumPagedRep<T> tSumPagedRep = getEntityById(id);
        return getEntityRepresentation(tSumPagedRep.getData().get(0));
    }

    @Transactional
    public void rollback(String changeId) {
        log.info("start of rollback change /w id {}", changeId);
        String[] split = entityClass.getName().split("\\.");
        SumPagedRep<AppChangeRecordCardRep> appChangeRecordCardRepSumPagedRep = appChangeRecordApplicationService.readByQuery(CHANGE_ID + ":" + changeId + CHANGE_REVOKED + "," + ENTITY_TYPE + ":" + split[split.length - 1], null, "sc:1");
        if (appChangeRecordCardRepSumPagedRep.getData() != null && appChangeRecordCardRepSumPagedRep.getData().size() > 0) {
            throw new HangingTransactionException();
        }
        SumPagedRep<AppChangeRecordCardRep> appChangeRecordCardRepSumPagedRep1 = appChangeRecordApplicationService.readByQuery(CHANGE_ID + ":" + changeId + "," + ENTITY_TYPE + ":" + split[split.length - 1], null, "sc:1");
        List<AppChangeRecordCardRep> data = appChangeRecordCardRepSumPagedRep1.getData();
        if (data == null || data.size() == 0) {
            throw new ChangeNotFoundException();
        }
        if ((data.get(0).getOperationType().equals(OperationType.DELETE_BY_ID)
                || data.get(0).getOperationType().equals(OperationType.DELETE_BY_QUERY)
                || data.get(0).getOperationType().equals(OperationType.POST)
        )) {
            if (data.get(0).getOperationType().equals(OperationType.POST)) {
                saveChangeRecord(null, changeId + CHANGE_REVOKED, OperationType.CANCEL_CREATE, data.get(0).getQuery());
                doDelete(data.get(0).getQuery());
            } else {
                restoreDelete(data.get(0).getQuery().replace("id:", ""), changeId + CHANGE_REVOKED);
            }
        } else if (data.get(0).getOperationType().equals(OperationType.PATCH_BATCH)) {
            List<PatchCommand> rollbackCmd = buildRollbackCommand(data.get(0).getPatchCommands());
            patchBatch(rollbackCmd, changeId + CHANGE_REVOKED);
        } else {
            throw new RollbackNotSupportedException();
        }
    }

    private List<PatchCommand> buildRollbackCommand(List<PatchCommand> patchCommands) {
        List<PatchCommand> deepCopy = getDeepCopy(patchCommands);
        deepCopy.forEach(e -> {
            if (e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_SUM)) {
                e.setOp(PATCH_OP_TYPE_DIFF);
            } else if (e.getOp().equalsIgnoreCase(PATCH_OP_TYPE_DIFF)) {
                e.setOp(PATCH_OP_TYPE_SUM);
            } else {
                throw new RollbackNotSupportedException();
            }
        });
        return deepCopy;
    }

    private void restoreDelete(String ids, String changeId) {
        saveChangeRecord(null, changeId, OperationType.RESTORE_DELETE, "id:" + ids);
        String[] split = ids.split(".");
        for (String str : split) {
            Optional<T> byId = repo.findById(Long.parseLong(str));//use repo instead of common readyBy
            if (byId.isEmpty())
                throw new EntityNotExistException();
            T t = byId.get();
            t.setDeleted(false);
            t.setRestoredAt(new Date());
            Optional<String> currentAuditor = AuditorAwareImpl.getAuditor();
            t.setRestoredBy(currentAuditor.orElse(""));
            repo.save(byId.get());
        }
    }

    protected SumPagedRep<T> getEntityById(Long id) {
        SumPagedRep<T> tSumPagedRep = queryRegistry.readById(role, id.toString(), entityClass);
        if (tSumPagedRep.getData().size() == 0)
            throw new EntityNotExistException();
        return tSumPagedRep;
    }

    protected List<PatchCommand> getDeepCopy(List<PatchCommand> patchCommands) {
        List<PatchCommand> deepCopy;
        try {
            deepCopy = om.readValue(om.writeValueAsString(patchCommands), new TypeReference<List<PatchCommand>>() {
            });
        } catch (IOException e) {
            log.error("error during deep copy", e);
            throw new DeepCopyException();
        }
        return deepCopy;
    }

    protected void saveChangeRecord(List<PatchCommand> patchCommands, String changeId, OperationType operationType, String query) {
        AppCreateChangeRecordCommand changeRecord = new AppCreateChangeRecordCommand();
        changeRecord.setPatchCommands((ArrayList<PatchCommand>) patchCommands);
        changeRecord.setChangeId(changeId);
        String[] split = entityClass.getName().split("\\.");
        changeRecord.setEntityType(split[split.length - 1]);
        changeRecord.setServiceBeanName(this.getClass().getName());
        changeRecord.setOperationType(operationType);
        changeRecord.setQuery(query);
        appChangeRecordApplicationService.create(changeRecord);
    }

    protected List<X> getAllByQuery(String query) {
        SumPagedRep<X> sumPagedRep = readByQuery(query, null, null);
        List<X> data = sumPagedRep.getData();
        if (data.size() == 0)
            return data;
        long l = sumPagedRep.getTotalItemCount() / data.size();
        double ceil = Math.ceil(l);
        int count = BigDecimal.valueOf(ceil).intValue();
        for (int i = 1; i < count; i++) {
            SumPagedRep<X> next = readByQuery(query, "num:" + i, "sc:1");
            data.addAll(next.getData());
        }
        return data;
    }

    private CreatedEntityRep getCreatedEntityRepresentation(T created) {
        return new CreatedEntityRep(created);
    }

    public abstract T replaceEntity(T t, Object command);


    public abstract X getEntitySumRepresentation(T t);

    public abstract Y getEntityRepresentation(T t);

    protected abstract T createEntity(long id, Object command);

    public abstract void preDelete(T t);

    public abstract void postDelete(T t);

    protected abstract void prePatch(T t, Map<String, Object> params, Z middleLayer);

    protected abstract void postPatch(T t, Map<String, Object> params, Z middleLayer);
}
