package com.mt.access.domain.model.revoke_token;

import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class RevokeTokenQuery extends QueryCriteria {
    private Set<RevokeTokenId> revokeTokenId;

    public RevokeTokenQuery(String queryParam, String pageParam, String config) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam);
        Optional.ofNullable(stringStringMap.get("targetId")).ifPresent(e -> revokeTokenId = Arrays.stream(e.split("\\$")).map(RevokeTokenId::new).collect(Collectors.toSet()));
        setPageConfig(PageConfig.limited(pageParam, 2000));
        setQueryConfig(new QueryConfig(config));
    }
}
