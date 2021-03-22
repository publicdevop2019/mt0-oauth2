package com.mt.access.port.adapter.persistence.user;


import com.mt.common.CommonConstant;
import com.mt.common.domain.model.restful.exception.UpdateFiledValueException;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.sql.builder.UpdateByIdQueryBuilder;
import com.mt.access.domain.model.user.User;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.mt.access.domain.model.user.User.ENTITY_LOCKED;

@Component
public class UpdateUserQueryBuilder extends UpdateByIdQueryBuilder<User> {
    {
        filedMap.put(ENTITY_LOCKED, ENTITY_LOCKED);
        filedTypeMap.put(ENTITY_LOCKED, this::parseBoolean);
    }

    @Override
    public Predicate getWhereClause(Root<User> root, List<String> query, PatchCommand command) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        List<Predicate> results = new ArrayList<>();
        for (String str : query) {
            Predicate equal = cb.equal(root.get("userId").get(CommonConstant.DOMAIN_ID), str);
            results.add(equal);
        }
        return cb.or(results.toArray(new Predicate[0]));
    }

    private Boolean parseBoolean(@Nullable Object input) {
        if (input == null)
            throw new UpdateFiledValueException();
        if (input.getClass().equals(Boolean.class))
            return ((Boolean) input);
        return Boolean.parseBoolean((String) input);
    }

}
