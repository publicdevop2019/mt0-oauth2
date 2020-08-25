package com.hw.shared.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.hw.shared.DeepCopyException;
import com.hw.shared.IdGenerator;
import com.hw.shared.idempotent.ChangeRecord;
import com.hw.shared.idempotent.ChangeRepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class DefaultRoleBasedRestfulService<T extends IdBasedEntity, X, Y, Z extends TypedClass<Z>> {

    protected JpaRepository<T, Long> repo;
    protected IdGenerator idGenerator;
    protected RestfulQueryRegistry<T> queryRegistry;

    protected Class<T> entityClass;

    protected Function<T, Z> entityPatchSupplier;

    protected RestfulQueryRegistry.RoleEnum role;
    protected ObjectMapper om;
    protected ChangeRepository changeRepository;

    @Transactional
    public CreatedEntityRep create(Object command, String changeId) {
        saveChangeRecord(null, changeId);
        T created = createEntity(idGenerator.getId(), command);
        repo.save(created);
        return getCreatedEntityRepresentation(created);
    }

    @Transactional
    public void replaceById(Long id, Object command, String changeId) {
        saveChangeRecord(null, changeId);
        SumPagedRep<T> tSumPagedRep = getEntityById(id);
        T after = replaceEntity(tSumPagedRep.getData().get(0), command);
        repo.save(after);
    }

    @Transactional
    public void patchById(Long id, JsonPatch patch, String changeId) {
        saveChangeRecord(null, changeId);
        SumPagedRep<T> entityById = getEntityById(id);
        T original = entityById.getData().get(0);
        Z command = entityPatchSupplier.apply(original);
        Z patchMiddleLayer;
        try {
            JsonNode jsonNode = om.convertValue(command, JsonNode.class);
            JsonNode patchedNode = patch.apply(jsonNode);
            patchMiddleLayer = om.treeToValue(patchedNode, command.getClazz());
        } catch (JsonPatchException | JsonProcessingException e) {
            e.printStackTrace();
            throw new EntityPatchException();
        }
        BeanUtils.copyProperties(patchMiddleLayer, original);
        repo.save(original);
    }

    @Transactional
    public Integer patchBatch(List<PatchCommand> commands, String changeId) {
        saveChangeRecord(commands, changeId);
        List<PatchCommand> deepCopy = getDeepCopy(commands);
        return queryRegistry.update(role, deepCopy, entityClass);
    }

    @Transactional
    public Integer deleteById(Long id) {
        return queryRegistry.deleteById(role, id.toString(), entityClass);
    }

    @Transactional
    public Integer deleteByQuery(String query) {
        return queryRegistry.deleteByQuery(role, query, entityClass);
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

    private SumPagedRep<T> getEntityById(Long id) {
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

    protected void saveChangeRecord(List<PatchCommand> details, String changeId) {
        ChangeRecord changeRecord = new ChangeRecord();
        changeRecord.setPatchCommands((ArrayList<PatchCommand>) details);
        changeRecord.setChangeId(changeId);
        changeRecord.setId(idGenerator.getId());
        changeRecord.setEntityType(entityClass.getName());
        changeRepository.save(changeRecord);
    }

    private CreatedEntityRep getCreatedEntityRepresentation(T created) {
        return new CreatedEntityRep(created);
    }

    public abstract T replaceEntity(T t, Object command);

    public abstract X getEntitySumRepresentation(T t);

    public abstract Y getEntityRepresentation(T t);

    protected abstract T createEntity(long id, Object command);
}
