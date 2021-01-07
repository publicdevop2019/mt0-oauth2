package com.mt.identityaccess.port.adapter.persistence.user;


import com.mt.common.rest.exception.UpdateFiledValueException;
import com.mt.common.sql.PatchCommand;
import com.mt.common.sql.builder.UpdateByIdQueryBuilder;
import com.mt.identityaccess.domain.model.user.User;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static com.mt.identityaccess.domain.model.user.User.ENTITY_LOCKED;

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
            Predicate equal = cb.equal(root.get("userId").get("domainId"), str);
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
