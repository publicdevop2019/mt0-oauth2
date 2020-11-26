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
import com.hw.shared.idempotent.exception.RollbackNotSupportedException;
import com.hw.shared.idempotent.representation.AppChangeRecordCardRep;
import com.hw.shared.rest.exception.AggregateNotExistException;
import com.hw.shared.rest.exception.AggregateOutdatedException;
import com.hw.shared.rest.exception.AggregatePatchException;
import com.hw.shared.sql.PatchCommand;
import com.hw.shared.sql.RestfulQueryRegistry;
import com.hw.shared.sql.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.hw.shared.AppConstant.*;
import static com.hw.shared.idempotent.model.ChangeRecord.CHANGE_ID;
import static com.hw.shared.idempotent.model.ChangeRecord.ENTITY_TYPE;

@Slf4j
public abstract class DefaultRoleBasedRestfulService<T extends Auditable & Aggregate, X, Y, Z extends TypedClass<Z>> implements AfterWriteCompleteHook<T> {
    @Autowired
    protected JpaRepository<T, Long> repo;
    @Autowired
    protected IdGenerator idGenerator;
    @Autowired
    protected RestfulQueryRegistry<T> queryRegistry;
    @Autowired
    protected StringRedisTemplate redisTemplate;

    protected Class<T> entityClass;

    protected Function<T, Z> entityPatchSupplier;

    protected RestfulQueryRegistry.RoleEnum role;
    @Autowired
    protected ObjectMapper om;
    @Autowired
    protected TransactionTemplate transactionTemplate;
    protected boolean rollbackSupported = true;
    @Autowired
    protected AppChangeRecordApplicationService appChangeRecordApplicationService;
    protected boolean deleteHook = false;

    protected Long getAggregateId(Object object) {
        return idGenerator.getId();
    }

    public CreatedAggregateRep create(Object command, String changeId) {
        if (changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
            return new CreatedAggregateRep();
        } else if (changeAlreadyExist(changeId) && !changeAlreadyRevoked(changeId)) {
            String entityType = getEntityName();
            SumPagedRep<AppChangeRecordCardRep> appChangeRecordCardRepSumPagedRep = appChangeRecordApplicationService.readByQuery(CHANGE_ID + ":" + changeId + "," + ENTITY_TYPE + ":" + entityType, null, "sc:1");
            CreatedAggregateRep createdEntityRep = new CreatedAggregateRep();
            long l = Long.parseLong(appChangeRecordCardRepSumPagedRep.getData().get(0).getQuery().replace("id:", ""));
            createdEntityRep.setId(l);
            return createdEntityRep;
        } else if (!changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
            saveChangeRecord(command, changeId, OperationType.POST, "id:", null, null);
            return new CreatedAggregateRep();
        } else {
            long id = getAggregateId(null);
            T execute = transactionTemplate.execute(transactionStatus -> {
                saveChangeRecord(command, changeId, OperationType.POST, "id:" + id, null, null);
                T created = createEntity(id, command);
                return repo.save(created);
            });
            cleanUpCache(Collections.singleton(id));
            afterWriteComplete();
            afterCreateComplete(execute);
            return getCreatedEntityRepresentation(execute);
        }
    }

    public void replaceById(Long id, Object command, String changeId) {
        if (changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
        } else if (changeAlreadyExist(changeId) && !changeAlreadyRevoked(changeId)) {
        } else if (!changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
            saveChangeRecord(command, changeId, OperationType.PUT, "id:" + id.toString(), null, null);
        } else {
            SumPagedRep<T> tSumPagedRep = getEntityById(id);
            checkVersion(tSumPagedRep.getData().get(0), command);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    saveChangeRecord(command, changeId, OperationType.PUT, "id:" + id.toString(), null, tSumPagedRep.getData().get(0));
                    T after = replaceEntity(tSumPagedRep.getData().get(0), command);
                    repo.save(after);
                }
            });
            cleanUpCache(Collections.singleton(id));
            afterWriteComplete();
            afterReplaceByIdComplete(id);
        }
    }

    public void patchById(Long id, JsonPatch patch, Map<String, Object> params) {
        String changeId = (String) params.get(HTTP_HEADER_CHANGE_ID);
        if (changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
        } else if (changeAlreadyExist(changeId) && !changeAlreadyRevoked(changeId)) {
        } else if (!changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
            saveChangeRecord(patch, (String) params.get(HTTP_HEADER_CHANGE_ID), OperationType.PATCH_BY_ID, "id:" + id.toString(), null, null);
        } else {
            SumPagedRep<T> entityById = getEntityById(id);
            T original = entityById.getData().get(0);
            Z command = entityPatchSupplier.apply(original);
            try {
                JsonNode jsonNode = om.convertValue(command, JsonNode.class);
                JsonNode patchedNode = patch.apply(jsonNode);
                command = om.treeToValue(patchedNode, command.getClazz());
            } catch (JsonPatchException | JsonProcessingException e) {
                e.printStackTrace();
                throw new AggregatePatchException();
            }
            prePatch(original, params, command);
            BeanUtils.copyProperties(command, original);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                    saveChangeRecord(patch, (String) params.get(HTTP_HEADER_CHANGE_ID), OperationType.PATCH_BY_ID, "id:" + id.toString(), null, null);
                    repo.save(original);
                }
            });
            postPatch(original, params, command);
            cleanUpCache(Collections.singleton(id));
            afterWriteComplete();
            afterPatchByIdComplete(id);
        }
    }

    public Integer patchBatch(List<PatchCommand> commands, String changeId) {
        if (changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
            return 0;
        } else if (changeAlreadyExist(changeId) && !changeAlreadyRevoked(changeId)) {
            return 0;
        } else if (!changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
            saveChangeRecord(commands, changeId, OperationType.PATCH_BATCH, null, null, null);
            return 0;
        } else {
            List<PatchCommand> deepCopy = getDeepCopy(commands);
            Integer execute = transactionTemplate.execute(transactionStatus -> {
                saveChangeRecord(commands, changeId, OperationType.PATCH_BATCH, null, null, null);
                return queryRegistry.update(role, deepCopy, entityClass);
            });
            cleanUpAllCache();
            afterWriteComplete();
            afterPatchBatchComplete();
            return execute;
        }
    }

    public Integer deleteById(Long id, String changeId) {
        return deleteByQuery("id:" + id.toString(), changeId);
    }

    public Integer deleteByQuery(String query, String changeId) {
        if (changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
            return 0;
        } else if (changeAlreadyExist(changeId) && !changeAlreadyRevoked(changeId)) {
            return 0;
        } else if (!changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
            saveChangeRecord(null, changeId, OperationType.DELETE_BY_QUERY, query, Collections.EMPTY_SET, null);
            return 0;
        } else {
            List<T> data = getTs(query);
            if (data.isEmpty())
                return 0;
            if (deleteHook) {
                data.forEach(this::preDelete);
            }
            Set<Long> collect = data.stream().map(Aggregate::getId).collect(Collectors.toSet());
            String join = "id:" + String.join(".", collect.stream().map(Object::toString).collect(Collectors.toSet()));
            Integer execute = transactionTemplate.execute(transactionStatus -> {
                saveChangeRecord(null, changeId, OperationType.DELETE_BY_QUERY, query, collect, null);
                return queryRegistry.deleteByQuery(role, join, entityClass);//delete only checked entity
            });
            if (deleteHook) {
                data.forEach(this::postDelete);
            }
            cleanUpCache(collect);
            afterWriteComplete();
            afterDeleteComplete(collect);
            return execute;
        }

    }


    private List<T> getTs(String query) {
        int pageNum = 0;
        SumPagedRep<T> tSumPagedRep = queryRegistry.readByQuery(role, query, "num:" + pageNum, null, entityClass);
        if (tSumPagedRep.getData().size() == 0)
            return new ArrayList<>();
        double l = (double) tSumPagedRep.getTotalItemCount() / tSumPagedRep.getData().size();//for accuracy
        double ceil = Math.ceil(l);
        int i = BigDecimal.valueOf(ceil).intValue();
        List<T> data = new ArrayList<>(tSumPagedRep.getData());
        for (int a = 1; a < i; a++) {
            data.addAll(queryRegistry.readByQuery(role, query, "num:" + a, null, entityClass).getData());
        }
        return data;
    }

    public SumPagedRep<X> readByQuery(String query, String page, String config) {
        SumPagedRep<T> tSumPagedRep = queryRegistry.readByQuery(role, query, page, config, entityClass);
        List<X> col = tSumPagedRep.getData().stream().map(this::getEntitySumRepresentation).collect(Collectors.toList());
        return new SumPagedRep<>(col, tSumPagedRep.getTotalItemCount());
    }


    public Y readById(Long id) {
        SumPagedRep<T> tSumPagedRep = getEntityById(id);
        return getEntityRepresentation(tSumPagedRep.getData().get(0));
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
        String[] split = entityClass.getName().split("\\.");
        return split[split.length - 1];
    }

    public void rollback(String changeId) {
        if (!rollbackSupported) {
            log.debug(getEntityName() + " rollback not supported, ignoring rollback operation");
            return;
        }
        if (changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
        } else if (changeAlreadyExist(changeId) && !changeAlreadyRevoked(changeId)) {
            String entityType = getEntityName();
            log.info("start of rollback change /w id {}", changeId);
            SumPagedRep<AppChangeRecordCardRep> appChangeRecordCardRepSumPagedRep1 = appChangeRecordApplicationService.readByQuery(CHANGE_ID + ":" + changeId + "," + ENTITY_TYPE + ":" + entityType, null, "sc:1");
            List<AppChangeRecordCardRep> data = appChangeRecordCardRepSumPagedRep1.getData();
            if (data == null || data.size() == 0) {
                throw new ChangeNotFoundException();
            }
            if ((data.get(0).getOperationType().equals(OperationType.DELETE_BY_QUERY)
                    || data.get(0).getOperationType().equals(OperationType.POST)
            )) {
                if (data.get(0).getOperationType().equals(OperationType.POST)) {
                    Set<Long> execute = transactionTemplate.execute(e -> {
                        saveChangeRecord(null, changeId + CHANGE_REVOKED, OperationType.CANCEL_CREATE, data.get(0).getQuery(), null, null);
                        return restoreCreate(data.get(0).getQuery().replace("id:", ""));
                    });
                    afterWriteComplete();
                    afterDeleteComplete(execute);
                    cleanUpCache(execute);
                } else {
                    String collect = data.get(0).getDeletedIds().stream().map(Object::toString).collect(Collectors.joining("."));
                    Set<Long> execute = transactionTemplate.execute(e -> {
                        saveChangeRecord(null, changeId + CHANGE_REVOKED, OperationType.RESTORE_DELETE, "id:" + collect, null, null);
                        return restoreDelete(collect);
                    });
                    afterWriteComplete();
                    afterCreateComplete(null);
                    cleanUpCache(execute);
                }
            } else if (data.get(0).getOperationType().equals(OperationType.PATCH_BATCH)) {
                List<PatchCommand> rollbackCmd = buildRollbackCommand((List<PatchCommand>) data.get(0).getRequestBody());
                patchBatch(rollbackCmd, changeId + CHANGE_REVOKED);
            } else if (data.get(0).getOperationType().equals(OperationType.PUT)) {
                T previous = (T) data.get(0).getReplacedVersion();
                T stored = getEntityById(previous.getId()).getData().get(0);
                Integer version = stored.getVersion();
                Integer version1 = previous.getVersion();
                if (version - 1 == version1) {
                    log.info("restore to previous entity version");
                } else {
                    log.warn("stored previous version is out dated, your data may get lost");
                }
                BeanUtils.copyProperties(previous, stored);
                Set<Long> execute = transactionTemplate.execute(e -> {
                    saveChangeRecord(null, changeId + CHANGE_REVOKED, OperationType.RESTORE_LAST_VERSION, data.get(0).getQuery(), null, null);
                    repo.save(stored);
                    return Collections.singleton(stored.getId());
                });
                afterWriteComplete();
                afterReplaceByIdComplete(stored.getId());
                cleanUpCache(execute);
            } else {
                throw new RollbackNotSupportedException();
            }
            log.info("end of rollback change /w id {}", changeId);
        } else if (!changeAlreadyExist(changeId) && changeAlreadyRevoked(changeId)) {
        } else {
            saveChangeRecord(null, changeId + CHANGE_REVOKED, OperationType.EMPTY_OPT, null, null, null);
        }
    }

    protected List<PatchCommand> buildRollbackCommand(List<PatchCommand> patchCommands) {
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

    protected Set<Long> restoreDelete(String ids) {
        Set<Long> collect = Arrays.stream(ids.split("\\.")).map(Long::parseLong).collect(Collectors.toSet());
        for (Long l : collect) {
            Optional<T> byId = repo.findById(l);//use repo instead of common readyBy
            if (byId.isEmpty())
                throw new AggregateNotExistException();
            T t = byId.get();
            t.setDeleted(false);
            t.setRestoredAt(new Date());
            Optional<String> currentAuditor = AuditorAwareImpl.getAuditor();
            t.setRestoredBy(currentAuditor.orElse(""));
            repo.save(byId.get());
        }
        return collect;
    }

    protected Set<Long> restoreCreate(String ids) {
        Set<Long> collect = Arrays.stream(ids.split("\\.")).map(Long::parseLong).collect(Collectors.toSet());
        for (Long l : collect) {
            Optional<T> byId = repo.findById(l);//use repo instead of common readyBy
            if (byId.isEmpty())
                throw new AggregateNotExistException();
            T t = byId.get();
            t.setDeleted(true);
            t.setDeletedAt(new Date());
            Optional<String> currentAuditor = AuditorAwareImpl.getAuditor();
            t.setDeletedBy(currentAuditor.orElse(""));
            repo.save(byId.get());
        }
        return collect;
    }

    protected SumPagedRep<T> getEntityById(Long id) {
        SumPagedRep<T> tSumPagedRep = queryRegistry.readById(role, id.toString(), entityClass);
        if (tSumPagedRep.getData().size() == 0)
            throw new AggregateNotExistException();
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

    private CreatedAggregateRep getCreatedEntityRepresentation(T created) {
        return new CreatedAggregateRep(created);
    }

    private void cleanUpCache(Set<Long> ids) {
        log.info("clean up cache");
        if (hasCachedAggregates()) {
            String entityName = getEntityName();
            Set<String> keys = redisTemplate.keys(entityName + CACHE_QUERY_PREFIX + ":*");
            if (!CollectionUtils.isEmpty(keys)) {
                redisTemplate.delete(keys);
            }
            ids.forEach(id -> {
                Set<String> keys1 = redisTemplate.keys(entityName + CACHE_ID_PREFIX + ":*");
                if (!CollectionUtils.isEmpty(keys1)) {
                    Set<String> collect = keys1.stream().filter(e -> {
                        String[] split1 = e.split(":");
                        String[] split2 = split1[1].split("\\[");
                        String s = split2[split2.length - 1];
                        String replace = s.replace("]", "");
                        String[] split3 = replace.split("-");
                        long min = Long.parseLong(split3[0]);
                        long max = Long.parseLong(split3[1]);
                        return id <= max && id >= min;
                    }).collect(Collectors.toSet());
                    if (!CollectionUtils.isEmpty(collect)) {
                        redisTemplate.delete(collect);
                    }
                }
            });
        }
    }

    private boolean hasCachedAggregates() {
        return queryRegistry.cacheable.keySet().stream().anyMatch(e -> queryRegistry.cacheable.get(e));

    }

    private void cleanUpAllCache() {
        if (hasCachedAggregates()) {
            String entityName = getEntityName();
            Set<String> keys = redisTemplate.keys(entityName + CACHE_QUERY_PREFIX + ":*");
            if (!CollectionUtils.isEmpty(keys)) {
                redisTemplate.delete(keys);
            }
            Set<String> keys1 = redisTemplate.keys(entityName + CACHE_ID_PREFIX + ":*");
            if (!CollectionUtils.isEmpty(keys1)) {
                redisTemplate.delete(keys1);
            }
        }
    }

    private void checkVersion(T t, Object command) {
        if (command instanceof AggregateUpdateCommand) {
            if (!t.getVersion().equals(((AggregateUpdateCommand) command).getVersion())) {
                throw new AggregateOutdatedException();
            }
        }
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
