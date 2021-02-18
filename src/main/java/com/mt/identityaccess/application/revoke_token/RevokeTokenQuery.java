package com.mt.identityaccess.application.revoke_token;

import com.mt.common.query.QueryCriteria;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RevokeTokenQuery extends QueryCriteria {

    public static final String TARGET_ID = "targetId";
    private final Set<String> revokeTokenId = new HashSet<>();

    public RevokeTokenQuery(String queryParam) {
        super(queryParam);
        if (parsed.size() > 1 || (parsed.size() != 0 && parsed.get(TARGET_ID) == null)) {
            throw new QueryParseException();
        }
        if (!isGetAll()) {
            String s = parsed.get(TARGET_ID);
            if (s.contains("$")) {
                String[] split = s.split("\\$");
                revokeTokenId.addAll(List.of(split));
            } else {
                revokeTokenId.add(s);
            }
        }
    }

    public Set<String> getRevokeTokenId() {
        return revokeTokenId;
    }
}
