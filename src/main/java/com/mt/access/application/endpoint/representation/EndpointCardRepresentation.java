package com.mt.access.application.endpoint.representation;

import com.mt.access.domain.model.endpoint.Endpoint;
import lombok.Data;

import java.util.Set;

@Data
public class EndpointCardRepresentation {
    private String id;
    private String description;
    private String resourceId;
    private String path;
    private String method;
    private Integer version;
    private Set<String> clientRoles;
    private Set<String> userRoles;
    private Set<String> clientScopes;
    private boolean websocket;
    private boolean csrfEnabled;
    private boolean secured;
    private boolean userOnly;
    private boolean clientOnly;
    public EndpointCardRepresentation(Object o) {
        Endpoint endpoint = (Endpoint) o;
        this.id = endpoint.getEndpointId().getDomainId();
        this.description = endpoint.getDescription();
        this.websocket = endpoint.isWebsocket();
        this.resourceId = endpoint.getClientId().getDomainId();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.version = endpoint.getVersion();
        this.clientRoles = endpoint.getClientRoles();
        this.userRoles = endpoint.getUserRoles();
        this.clientScopes = endpoint.getClientScopes();
        this.secured = endpoint.isSecured();
        this.userOnly = endpoint.isUserOnly();
        this.clientOnly = endpoint.isClientOnly();
        this.csrfEnabled = endpoint.isCsrfEnabled();
    }
}
