package com.mt.identityaccess.domain.model.endpoint;


import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.identityaccess.domain.model.client.ClientId;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class EndpointQuery extends QueryCriteria {
    private Set<EndpointId> endpointIds;
    private Set<ClientId> clientIds;
    private String path;
    private String method;
    private EndpointSort endpointSort;

    public EndpointQuery(String queryParam) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
        setEndpointSort(pageConfig);
    }

    public EndpointQuery(EndpointId endpointId) {
        endpointIds = new HashSet<>(List.of(endpointId));
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
        setEndpointSort(pageConfig);
    }

    public EndpointQuery(String queryParam, String pageParam, String config) {
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.limited(pageParam, 40));
        setQueryConfig(new QueryConfig(config));
        setEndpointSort(pageConfig);
    }

    public EndpointQuery(DomainId domainId) {
        clientIds = new HashSet<>(List.of(new ClientId(domainId.getDomainId())));
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
        setEndpointSort(pageConfig);
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam);
        Optional.ofNullable(stringStringMap.get("id")).ifPresent(e -> {
            endpointIds = Arrays.stream(e.split("\\.")).map(EndpointId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get("resourceId")).ifPresent(e -> {
            clientIds = Arrays.stream(e.split("\\.")).map(ClientId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get("path")).ifPresent(e -> path = e);
        Optional.ofNullable(stringStringMap.get("method")).ifPresent(e -> method = e);
    }

    private void setEndpointSort(PageConfig pageConfig) {
        if (pageConfig.getSortBy().equalsIgnoreCase("id")) {
            this.endpointSort = EndpointSort.byId(pageConfig.isSortOrderAsc());
        }
        if (pageConfig.getSortBy().equalsIgnoreCase("resourceId")) {
            this.endpointSort = EndpointSort.byResourceId(pageConfig.isSortOrderAsc());
        }
        if (pageConfig.getSortBy().equalsIgnoreCase("path")) {
            this.endpointSort = EndpointSort.byPath(pageConfig.isSortOrderAsc());
        }
        if (pageConfig.getSortBy().equalsIgnoreCase("method")) {
            this.endpointSort = EndpointSort.byMethod(pageConfig.isSortOrderAsc());
        }
    }

    @Getter
    public static class EndpointSort {
        private boolean byId;
        private boolean byClientId;
        private boolean byMethod;
        private boolean byPath;
        private final boolean isAsc;

        private EndpointSort(boolean isAsc) {
            this.isAsc = isAsc;
        }

        public static EndpointSort byId(boolean isAsc) {
            EndpointSort endpointSort = new EndpointSort(isAsc);
            endpointSort.byId = true;
            return endpointSort;
        }

        public static EndpointSort byResourceId(boolean isAsc) {
            EndpointSort endpointSort = new EndpointSort(isAsc);
            endpointSort.byClientId = true;
            return endpointSort;
        }

        public static EndpointSort byMethod(boolean isAsc) {
            EndpointSort endpointSort = new EndpointSort(isAsc);
            endpointSort.byMethod = true;
            return endpointSort;
        }

        public static EndpointSort byPath(boolean isAsc) {
            EndpointSort endpointSort = new EndpointSort(isAsc);
            endpointSort.byPath = true;
            return endpointSort;
        }
    }
}
