package com.mt.access.application.endpoint.representation;

import com.mt.access.domain.model.endpoint.Endpoint;
import lombok.Data;

import java.util.Set;

@Data
public class EndpointRepresentation {
    private String id;
    private String resourceId;
    private String description;
    private String path;
    private String method;
    private String createdBy;
    private Long createdAt;
    private String modifiedBy;
    private Long modifiedAt;
    private Set<String> clientRoles;
    private Set<String> userRoles;
    private Set<String> clientScopes;
    private boolean secured;
    private boolean userOnly;
    private boolean clientOnly;
    private Integer version;

    public EndpointRepresentation(Endpoint endpoint) {
        this.id = endpoint.getEndpointId().getDomainId();
        this.clientRoles = endpoint.getClientRoles();
        this.userRoles = endpoint.getUserRoles();
        this.clientScopes = endpoint.getClientScopes();
        this.secured = endpoint.isSecured();
        this.userOnly = endpoint.isUserOnly();
        this.clientOnly = endpoint.isClientOnly();
        this.resourceId = endpoint.getClientId().getDomainId();
        this.description = endpoint.getDescription();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.createdBy = endpoint.getCreatedBy();
        this.createdAt = endpoint.getCreatedAt() != null ? endpoint.getCreatedAt().getTime() : null;
        this.modifiedBy = endpoint.getModifiedBy();
        this.modifiedAt = endpoint.getModifiedAt() != null ? endpoint.getModifiedAt().getTime() : null;
        this.version = endpoint.getVersion();
    }
}
