package com.mt.access.domain.model.client;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.common.domain.model.restful.query.QueryCriteria;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.Role;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class ClientQuery extends QueryCriteria {
    private Set<ClientId> clientIds;
    @Setter(AccessLevel.PRIVATE)
    private Set<ClientId> resources;
    private Boolean resourceFlag;
    private String name;
    private String authoritiesSearch;
    private String scopeSearch;
    private String grantTypeSearch;
    private String accessTokenSecSearch;

    private boolean isInternal = true;

    public ClientQuery(ClientId clientId) {
        clientIds = new HashSet<>(List.of(clientId));
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.skipCount());
    }

    public ClientQuery(String queryParam, boolean isInternal) {
        this.isInternal = isInternal;
        updateQueryParam(queryParam);
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public ClientQuery(DomainId domainId) {
        this.resources = new HashSet<>(List.of(new ClientId(domainId.getDomainId())));
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public ClientQuery(String queryParam, String pageConfig, String queryConfig, boolean isInternal) {
        this.isInternal = isInternal;
        setPageConfig(PageConfig.limited(pageConfig, 2000));
        setQueryConfig(new QueryConfig(queryConfig));
        updateQueryParam(queryParam);
        validate();
    }

    public ClientQuery(Set<ClientId> clientIds) {
        this.clientIds = clientIds;
        setPageConfig(PageConfig.defaultConfig());
        setQueryConfig(QueryConfig.countRequired());
    }

    public static ClientQuery resourceIds(ClientId removedClientId) {
        ClientQuery clientQuery = new ClientQuery();
        clientQuery.resources = new HashSet<>(List.of(removedClientId));
        clientQuery.setPageConfig(PageConfig.defaultConfig());
        clientQuery.setQueryConfig(QueryConfig.countRequired());
        return clientQuery;
    }

    private ClientQuery() {
    }

    private void updateQueryParam(String queryParam) {
        Map<String, String> stringStringMap = QueryUtility.parseQuery(queryParam);
        Optional.ofNullable(stringStringMap.get("id")).ifPresent(e -> {
            clientIds = Arrays.stream(e.split("\\.")).map(ClientId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get("clientId")).ifPresent(e -> {
            clientIds = Arrays.stream(e.split("\\.")).map(ClientId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get("resourceIndicator")).ifPresent(e -> {
            resourceFlag = e.equalsIgnoreCase("1");
        });
        Optional.ofNullable(stringStringMap.get("name")).ifPresent(e -> name = e);
        Optional.ofNullable(stringStringMap.get("grantTypeEnums")).ifPresent(e -> grantTypeSearch = e);
        Optional.ofNullable(stringStringMap.get("grantedAuthorities")).ifPresent(e -> authoritiesSearch = e);
        Optional.ofNullable(stringStringMap.get("scopeEnums")).ifPresent(e -> scopeSearch = e);
        Optional.ofNullable(stringStringMap.get("resourceIds")).ifPresent(e -> {
            resources = Arrays.stream(e.split("\\$")).map(ClientId::new).collect(Collectors.toSet());
        });
        Optional.ofNullable(stringStringMap.get("accessTokenValiditySeconds")).ifPresent(e -> accessTokenSecSearch = e);
    }

    private void validate() {
        if (!isInternal) {
            boolean isRoot = DomainRegistry.authenticationService().isUser()
                    && DomainRegistry.authenticationService().userInRole(Role.ROLE_ROOT);
            boolean isUser = DomainRegistry.authenticationService().isUser()
                    && DomainRegistry.authenticationService().userInRole(Role.ROLE_USER);
            if (isRoot || isUser) {
                if (!isRoot) {
                    if (clientIds == null)
                        throw new IllegalArgumentException("only root role allows empty query");
                    if (resources != null
                            || resourceFlag != null
                            || name != null
                            || authoritiesSearch != null
                            || scopeSearch != null
                            || grantTypeSearch != null
                            || accessTokenSecSearch != null)
                        throw new IllegalArgumentException("user role can only query by id");
                }
            } else {
                throw new IllegalArgumentException("only root/user role allows empty query");
            }
        }
    }

}
