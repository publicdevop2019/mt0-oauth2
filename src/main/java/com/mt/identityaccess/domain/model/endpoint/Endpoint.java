package com.mt.identityaccess.domain.model.endpoint;

import com.mt.common.Auditable;
import com.mt.common.domain.model.DomainEventPublisher;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.endpoint.event.EndpointUpdated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"domainId", "path", "method"}))
@Slf4j
@NoArgsConstructor
@Getter
@Where(clause = "deleted=0")
public class Endpoint extends Auditable {
    public static final String ENTITY_PATH = "path";
    public static final String ENTITY_METHOD = "method";
    public static final String ENTITY_EXPRESSION = "expression";
    /**
     * spring security style expression e.g. "hasRole('ROLE_USER') and #oauth2.hasScope('trust') and #oauth2.isUser()"
     * for public access this filed can be null
     */
    @Setter(AccessLevel.PRIVATE)
    private String expression;
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

    public Endpoint(ClientId clientId, String expression, String description, @NotBlank String path, EndpointId endpointId, @NotBlank String method) {
        setId(DomainRegistry.uniqueIdGeneratorService().id());
        setExpression(expression);
        setDescription(description);
        setClientId(clientId);
        setEndpointId(endpointId);
        setPath(path);
        setMethod(method);
    }

    public void replace(String expression, String description, @NotBlank String path, @NotBlank String method) {
        setExpression(expression);
        setDescription(description);
        setPath(path);
        setMethod(method);
        DomainEventPublisher.instance().publish(new EndpointUpdated(endpointId));
    }
}
