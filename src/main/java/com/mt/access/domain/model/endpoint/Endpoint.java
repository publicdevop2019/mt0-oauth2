package com.mt.access.domain.model.endpoint;

import com.google.common.base.Objects;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.endpoint.event.EndpointCollectionModified;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.sql.converter.StringSetConverter;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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
    @Setter(AccessLevel.PRIVATE)
    private boolean isWebsocket;
    @Setter(AccessLevel.PRIVATE)
    @Embedded
    private CorsConfig corsConfig;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    @AttributeOverrides({
            @AttributeOverride(name = "domainId", column = @Column(name = "clientId", updatable = false, nullable = false))
    })
    private ClientId clientId;
    private String path;
    @Embedded
    @Setter(AccessLevel.PRIVATE)
    private EndpointId endpointId;
    @Setter(AccessLevel.PRIVATE)
    private String method;
    @Setter(AccessLevel.PRIVATE)
    private boolean csrfEnabled=true;
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    public Endpoint(ClientId clientId, Set<String> userRoles, Set<String> clientRoles, Set<String> scopes, String description,
                    String path, EndpointId endpointId, String method,
                    boolean secured, boolean userOnly, boolean clientOnly, boolean isWebsocket, boolean csrfEnabled,CorsConfig corsConfig
    ) {
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setClientId(clientId);
        setEndpointId(endpointId);
        update(userRoles, clientRoles, scopes, description, path, method, secured, userOnly, clientOnly, isWebsocket,csrfEnabled,corsConfig);
    }

    public void update(Set<String> userRoles, Set<String> clientRoles, Set<String> scopes,
                       String description, String path, String method, boolean secured,
                       boolean userOnly, boolean clientOnly, boolean isWebsocket,
                       boolean csrfEnabled,CorsConfig corsConfig) {
        setUserRoles(userRoles);
        setClientRoles(clientRoles);
        setClientScopes(scopes);
        setDescription(description);
        setWebsocket(isWebsocket);
        setPath(path);
        setMethod(method);
        setSecured(secured);
        setUserOnly(userOnly);
        setClientOnly(clientOnly);
        setCsrfEnabled(csrfEnabled);
        setCorsConfig(corsConfig);
        validate(new HttpValidationNotificationHandler());
        DomainRegistry.getEndpointValidationService().validate(this, new HttpValidationNotificationHandler());
    }

    private void setPath(String path) {
        Validator.notBlank(path);
        this.path = path;
    }

    @PreUpdate
    private void preUpdate() {
        DomainEventPublisher.instance().publish(new EndpointCollectionModified());
    }

    @Override
    public void validate(@NotNull ValidationNotificationHandler handler) {
        (new EndpointValidator(this, handler)).validate();
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
