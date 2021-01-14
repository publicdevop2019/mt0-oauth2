package com.mt.identityaccess.application.revoke_token;

import com.mt.common.query.DefaultQuery;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RevokeTokenQuery extends DefaultQuery {

    public static final String TARGET_ID = "targetId";
    private final Set<String> targetIds = new HashSet<>();

    public RevokeTokenQuery(String queryParam) {
        super(queryParam);
        if (parsed.size() > 1 || (parsed.size() != 0 && parsed.get(TARGET_ID) == null)) {
            throw new QueryParseException();
        }
        if (!getAll()) {
            String s = parsed.get(TARGET_ID);
            if (s.contains("$")) {
                String[] split = s.split("\\$");
                targetIds.addAll(List.of(split));
            } else {
                targetIds.add(s);
            }
        }
    }

    public Set<String> getTargetIds() {
        return targetIds;
    }
}
