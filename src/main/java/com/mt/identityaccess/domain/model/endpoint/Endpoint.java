package com.mt.identityaccess.domain.model.endpoint;

import com.google.common.base.Objects;
import com.mt.common.audit.Auditable;
import com.mt.common.domain_event.DomainEventPublisher;
import com.mt.common.persistence.StringSetConverter;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.endpoint.event.EndpointCollectionModified;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"domainId", "path", "method"}))
@Slf4j
@NoArgsConstructor
@Getter
@Where(clause = "deleted=0")
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Endpoint extends Auditable {
    public static final String ENTITY_PATH = "path";
    public static final String ENTITY_METHOD = "method";
    @Setter(AccessLevel.PRIVATE)
    @Convert(converter = StringSetConverter.class)
    private Set<String> clientRoles;
    @Setter(AccessLevel.PRIVATE)
    @Convert(converter = StringSetConverter.class)
    private Set<String> userRoles;
    @Setter(AccessLevel.PRIVATE)
    @Convert(converter = StringSetConverter.class)
    private Set<String> clientScopes;
    @Setter(AccessLevel.PRIVATE)
    private boolean secured;
    @Setter(AccessLevel.PRIVATE)
    private boolean userOnly;
    @Setter(AccessLevel.PRIVATE)
    private boolean clientOnly;

    @Setter(AccessLevel.PRIVATE)
    private String description;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "clientId", updatable = false, nullable = false))
    })
    private ClientId clientId;
    @NotBlank
    @Setter(AccessLevel.PRIVATE)
    private String path;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    private EndpointId endpointId;
    @NotBlank
    @Setter(AccessLevel.PRIVATE)
    private String method;
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;
    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    public Endpoint(ClientId clientId, Set<String> userRoles, Set<String> clientRoles, Set<String> scopes, String description,
                    @NotBlank String path, EndpointId endpointId, @NotBlank String method,
                    boolean secured, boolean userOnly, boolean clientOnly
    ) {
        setId(DomainRegistry.uniqueIdGeneratorService().id());
        setClientId(clientId);
        setEndpointId(endpointId);
        replace(userRoles, clientRoles, scopes, description, path, method, secured, userOnly, clientOnly);
    }

    public void replace(Set<String> userRoles, Set<String> clientRoles, Set<String> scopes, String description, @NotBlank String path, @NotBlank String method, boolean secured, boolean userOnly, boolean clientOnly) {
        setUserRoles(userRoles);
        setClientRoles(clientRoles);
        setClientScopes(scopes);
        setDescription(description);
        setPath(path);
        setMethod(method);
        setSecured(secured);
        if (userOnly && clientOnly) {
            throw new IllegalArgumentException("userOnly and clientOnly can not be true at same time");
        }
        setUserOnly(userOnly);
        setClientOnly(clientOnly);
    }

    @PreUpdate
    private void preUpdate() {
        DomainEventPublisher.instance().publish(new EndpointCollectionModified());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endpoint)) return false;
        if (!super.equals(o)) return false;
        Endpoint endpoint = (Endpoint) o;
        return Objects.equal(endpointId, endpoint.endpointId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), endpointId);
    }
}
