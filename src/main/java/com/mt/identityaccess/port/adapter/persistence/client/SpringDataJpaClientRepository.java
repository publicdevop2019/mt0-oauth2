package com.mt.identityaccess.port.adapter.persistence.client;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.sql.clause.OrderClause;
import com.mt.common.domain.model.sql.exception.UnsupportedQueryException;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.client.ClientQuery;
import com.mt.identityaccess.domain.model.client.ClientRepository;
import com.mt.identityaccess.port.adapter.persistence.QueryBuilderRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public interface SpringDataJpaClientRepository extends JpaRepository<Client, Long>, ClientRepository {

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id = ?1")
    void softDelete(Long id);

    @Modifying
    @Query("update #{#entityName} e set e.deleted=true where e.id in ?1")
    void softDeleteAll(Set<Long> id);

    default Optional<Client> clientOfId(ClientId clientId) {
        return QueryBuilderRegistry.getClientSelectQueryBuilder().execute(new ClientQuery(clientId)).findFirst();
    }

    default void add(Client client) {
        save(client);
    }

    default void remove(Client client) {
        softDelete(client.getId());
    }

    default void remove(Collection<Client> client) {
        softDeleteAll(client.stream().map(Client::getId).collect(Collectors.toSet()));
    }

    default SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery) {
        return QueryBuilderRegistry.getClientSelectQueryBuilder().execute(clientQuery);
    }

    @Component
    class JpaCriteriaApiClientAdaptor {
        public static final String ENTITY_NAME = "name";

        public SumPagedRep<Client> execute(ClientQuery clientQuery) {
            QueryUtility.QueryContext<Client> queryContext = QueryUtility.prepareContext(Client.class, clientQuery);
            Optional.ofNullable(clientQuery.getClientIds()).ifPresent(e -> QueryUtility.addDomainIdInPredicate(e.stream().map(DomainId::getDomainId).collect(Collectors.toSet()), "clientId", queryContext));
            Optional.ofNullable(clientQuery.getResourceFlag()).ifPresent(e -> QueryUtility.addBooleanEqualPredicate(e, "accessible", queryContext));
            Optional.ofNullable(clientQuery.getName()).ifPresent(e -> QueryUtility.addStringLikePredicate(e, ENTITY_NAME, queryContext));
            Optional.ofNullable(clientQuery.getAuthoritiesSearch()).ifPresent(e -> QueryUtility.addStringLikePredicate(e, "authorities", queryContext));
            Optional.ofNullable(clientQuery.getScopeSearch()).ifPresent(e -> QueryUtility.addStringLikePredicate(e, "scopes", queryContext));

            Optional.ofNullable(clientQuery.getGrantTypeSearch()).ifPresent(e -> {
                queryContext.getPredicates().add(GrantEnabledPredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot()));
                Optional.ofNullable(queryContext.getCountPredicates())
                        .ifPresent(ee -> ee.add(GrantEnabledPredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getCountRoot())));
            });
            Optional.ofNullable(clientQuery.getResources()).ifPresent(e -> {
                queryContext.getPredicates().add(ResourceIdsPredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot()));
                Optional.ofNullable(queryContext.getCountPredicates())
                        .ifPresent(ee -> ee.add(ResourceIdsPredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getCountRoot())));
            });
            Optional.ofNullable(clientQuery.getAccessTokenSecSearch()).ifPresent(e -> {
                queryContext.getPredicates().add(GrantAccessTokenClausePredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getRoot()));
                Optional.ofNullable(queryContext.getCountPredicates())
                        .ifPresent(ee -> ee.add(GrantAccessTokenClausePredicateConverter.getPredicate(e, queryContext.getCriteriaBuilder(), queryContext.getCountRoot())));
            });

            ClientOrderConverter clientSortConverter = new ClientOrderConverter();
            List<Order> orderClause = clientSortConverter.getOrderClause(clientQuery.getPageConfig().getRawValue(), queryContext.getCriteriaBuilder(), queryContext.getRoot(), queryContext.getQuery());
            queryContext.setOrder(orderClause);
            return QueryUtility.pagedQuery(clientQuery, queryContext);
        }

        public static class GrantAccessTokenClausePredicateConverter {

            public static Predicate getPredicate(String query, CriteriaBuilder cb, Root<Client> root) {
                String[] split = query.split("\\$");
                List<Predicate> results = new ArrayList<>();
                List<Predicate> results2 = new ArrayList<>();
                List<Predicate> results3 = new ArrayList<>();
                for (String str : split) {
                    if (str.contains("<=")) {
                        int i = Integer.parseInt(str.replace("<=", ""));
                        results.add(cb.lessThanOrEqualTo(root.get("clientCredentialsGrant").get("accessTokenValiditySeconds"), i));
                        results2.add(cb.lessThanOrEqualTo(root.get("authorizationCodeGrant").get("accessTokenValiditySeconds"), i));
                        results3.add(cb.lessThanOrEqualTo(root.get("passwordGrant").get("accessTokenValiditySeconds"), i));
                    } else if (str.contains(">=")) {
                        int i = Integer.parseInt(str.replace(">=", ""));
                        results.add(cb.greaterThanOrEqualTo(root.get("clientCredentialsGrant").get("accessTokenValiditySeconds"), i));
                        results2.add(cb.greaterThanOrEqualTo(root.get("authorizationCodeGrant").get("accessTokenValiditySeconds"), i));
                        results3.add(cb.greaterThanOrEqualTo(root.get("passwordGrant").get("accessTokenValiditySeconds"), i));
                    } else if (str.contains("<")) {
                        int i = Integer.parseInt(str.replace("<", ""));
                        results.add(cb.lessThan(root.get("clientCredentialsGrant").get("accessTokenValiditySeconds"), i));
                        results2.add(cb.lessThan(root.get("authorizationCodeGrant").get("accessTokenValiditySeconds"), i));
                        results3.add(cb.lessThan(root.get("passwordGrant").get("accessTokenValiditySeconds"), i));
                    } else if (str.contains(">")) {
                        int i = Integer.parseInt(str.replace(">", ""));
                        results.add(cb.greaterThan(root.get("clientCredentialsGrant").get("accessTokenValiditySeconds"), i));
                        results2.add(cb.greaterThan(root.get("authorizationCodeGrant").get("accessTokenValiditySeconds"), i));
                        results3.add(cb.greaterThan(root.get("passwordGrant").get("accessTokenValiditySeconds"), i));
                    } else {
                        throw new UnsupportedQueryException();
                    }
                }
                Predicate and = cb.and(results.toArray(new Predicate[0]));
                Predicate and2 = cb.and(results2.toArray(new Predicate[0]));
                Predicate and3 = cb.and(results3.toArray(new Predicate[0]));
                return cb.or(and, and2, and3);
            }
        }

        private static class GrantEnabledPredicateConverter {
            public static Predicate getPredicate(String query, CriteriaBuilder cb, Root<Client> root) {
                if (query.contains("$")) {
                    Set<String> strings = new TreeSet<>(Arrays.asList(query.split("\\$")));
                    List<Predicate> list2 = new ArrayList<>();
                    for (String str : strings) {
                        if ("CLIENT_CREDENTIALS".equalsIgnoreCase(str)) {
                            list2.add(cb.isTrue(root.get("clientCredentialsGrant").get("enabled")));
                        } else if ("PASSWORD".equalsIgnoreCase(str)) {
                            list2.add(cb.isTrue(root.get("passwordGrant").get("enabled")));
                        } else if ("AUTHORIZATION_CODE".equalsIgnoreCase(str)) {
                            list2.add(cb.isTrue(root.get("authorizationCodeGrant").get("enabled")));
                        } else if ("REFRESH_TOKEN".equalsIgnoreCase(str)) {
                            list2.add(cb.isTrue(root.get("passwordGrant").get("refreshTokenGrant").get("enabled")));
                        }
                    }
                    return cb.and(list2.toArray(Predicate[]::new));
                } else {
                    return getExpression(query, cb, root);
                }
            }

            private static Predicate getExpression(String str, CriteriaBuilder cb, Root<Client> root) {
                if ("CLIENT_CREDENTIALS".equalsIgnoreCase(str)) {
                    return cb.isTrue(root.get("clientCredentialsGrant").get("enabled"));
                } else if ("PASSWORD".equalsIgnoreCase(str)) {
                    return cb.isTrue(root.get("passwordGrant").get("enabled"));
                } else if ("AUTHORIZATION_CODE".equalsIgnoreCase(str)) {
                    return cb.isTrue(root.get("authorizationCodeGrant").get("enabled"));
                } else if ("REFRESH_TOKEN".equalsIgnoreCase(str)) {
                    return cb.isTrue(root.get("passwordGrant").get("refreshTokenGrant").get("enabled"));
                } else {
                    return null;
                }
            }
        }

        public static class ResourceIdsPredicateConverter {
            public static Predicate getPredicate(Set<ClientId> query, CriteriaBuilder cb, Root<Client> root) {
                Join<Object, Object> tags = root.join("resources");
                CriteriaBuilder.In<Object> clause = cb.in(tags.get("domainId"));
                query.forEach(e -> clause.value(e.getDomainId()));
                return clause;
            }
        }

        static class ClientOrderConverter extends OrderClause<Client> {
            @Override
            public List<Order> getOrderClause(String page, CriteriaBuilder cb, Root<Client> root, AbstractQuery<?> abstractQuery) {
                if (page == null) {
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
                        Order asc = cb.asc(root.get("clientId").get("domainId"));
                        return Collections.singletonList(asc);
                    } else {
                        Order desc = cb.desc(root.get("clientId").get("domainId"));
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
    }

}
