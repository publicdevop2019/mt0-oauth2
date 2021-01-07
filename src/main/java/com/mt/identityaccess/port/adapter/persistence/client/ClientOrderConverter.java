package com.mt.identityaccess.port.adapter.persistence.client;

import com.mt.common.sql.clause.OrderClause;
import com.mt.common.sql.exception.UnsupportedQueryException;
import com.mt.identityaccess.domain.model.client.Client;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ClientOrderConverter extends OrderClause<Client> {
    @Override
    public List<Order> getOrderClause(String page, CriteriaBuilder cb, Root<Client> root, AbstractQuery<?> abstractQuery) {
        if(page==null){
            Order asc = cb.asc(root.get("name"));
            return Collections.singletonList(asc);
        }
        String[] params = page.split(",");
        HashMap<String, String> orderMap = new HashMap<>();

        for (String param : params) {
            String[] values = param.split(":");
            if (values.length > 1) {
                if (values[0].equals("by") && values[1] != null) {
                    orderMap.put("by", values[1]);
                }
                if (values[0].equals("order") && values[1] != null) {
                    orderMap.put("order", values[1]);
                }
            }
        }
        if ("name".equalsIgnoreCase(orderMap.get("by"))) {
            if ("asc".equalsIgnoreCase(orderMap.get("order"))) {
                Order asc = cb.asc(root.get("name"));
                return Collections.singletonList(asc);
            } else {
                Order desc = cb.desc(root.get("name"));
                return Collections.singletonList(desc);
            }
        } else if ("resourceIndicator".equalsIgnoreCase(orderMap.get("by"))) {
            if ("asc".equalsIgnoreCase(orderMap.get("order"))) {
                Order asc = cb.asc(root.get("accessible"));
                return Collections.singletonList(asc);
            } else {
                Order desc = cb.desc(root.get("accessible"));
                return Collections.singletonList(desc);
            }
        } else if ("id".equalsIgnoreCase(orderMap.get("by"))) {
            if ("asc".equalsIgnoreCase(orderMap.get("order"))) {
                Order asc = cb.asc(root.get("clientId").get("clientId"));
                return Collections.singletonList(asc);
            } else {
                Order desc = cb.desc(root.get("clientId").get("clientId"));
                return Collections.singletonList(desc);
            }
        } else if ("accessTokenValiditySeconds".equalsIgnoreCase(orderMap.get("by"))) {
            if ("asc".equalsIgnoreCase(orderMap.get("order"))) {
                Order asc = cb.asc(root.get("clientCredentialsGrant").get("accessTokenValiditySeconds"));
                Order asc2 = cb.asc(root.get("authorizationCodeGrant").get("accessTokenValiditySeconds"));
                Order asc3 = cb.asc(root.get("passwordGrant").get("accessTokenValiditySeconds"));
                return Arrays.asList(asc, asc2, asc3);
            } else {
                Order desc = cb.desc(root.get("clientCredentialsGrant").get("accessTokenValiditySeconds"));
                Order desc1 = cb.desc(root.get("authorizationCodeGrant").get("accessTokenValiditySeconds"));
                Order desc2 = cb.desc(root.get("passwordGrant").get("accessTokenValiditySeconds"));
                return Arrays.asList(desc, desc1, desc2);
            }
        } else {
            //default sort
            if (orderMap.get("by") == null) {
                Order asc = cb.asc(root.get("name"));
                return Collections.singletonList(asc);
            } else {
                throw new UnsupportedQueryException();
            }
        }
    }
}
