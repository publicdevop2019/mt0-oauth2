package com.hw.shared.sql.builder;


import com.hw.shared.Auditable;
import com.hw.shared.rest.exception.NoUpdatableFieldException;
import com.hw.shared.rest.exception.UnsupportedPatchOperationException;
import com.hw.shared.sql.PatchCommand;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.hw.shared.AppConstant.*;

@Component
public abstract class UpdateByIdQueryBuilder<T extends Auditable> extends UpdateQueryBuilder<T> {
    protected Map<String, String> filedMap = new HashMap<>();
    protected Map<String, Function<Object, ?>> filedTypeMap = new HashMap<>();

    @Override
    protected void setUpdateValue(Root<T> root, CriteriaUpdate<T> criteriaUpdate, PatchCommand command) {
        ArrayList<Boolean> booleans = new ArrayList<>();
        filedMap.keySet().forEach(e -> {
            booleans.add(setUpdateValueFor(e, filedMap.get(e), criteriaUpdate, command));
        });
        Boolean hasFieldChange = booleans.stream().reduce(false, (a, b) -> a || b);
        if (!hasFieldChange) {
            throw new NoUpdatableFieldException();
        }
    }

    private boolean setUpdateValueFor(String fieldPath, String fieldLiteral, CriteriaUpdate<T> criteriaUpdate, PatchCommand command) {
        if (command.getPath().contains(fieldPath)) {
            if (command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_REMOVE)) {
                criteriaUpdate.set(fieldLiteral, null);
                return true;
            } else if (command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_ADD) || command.getOp().equalsIgnoreCase(PATCH_OP_TYPE_REPLACE)) {
                if (command.getValue() != null) {
                    criteriaUpdate.set(fieldLiteral, filedTypeMap.get(fieldPath).apply(command.getValue()));
                } else {
                    criteriaUpdate.set(fieldLiteral, null);
                }
                return true;
            } else {
                throw new UnsupportedPatchOperationException();
            }
        } else {
            return false;
        }
    }

    @Override
    public Predicate getWhereClause(Root<T> root, List<String> query, PatchCommand command) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> results = new ArrayList<>();
        for (String str : query) {
            Predicate equal = cb.equal(root.get(COMMON_ENTITY_ID), Long.parseLong(str));
            results.add(equal);
        }
        return cb.or(results.toArray(new Predicate[0]));
    }


}
