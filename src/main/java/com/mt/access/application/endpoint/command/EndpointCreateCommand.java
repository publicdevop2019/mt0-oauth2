package com.mt.access.application.endpoint.command;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class EndpointCreateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String description;
    private Set<String> clientRoles;
    private Set<String> userRoles;
    private Set<String> clientScopes;
    private boolean secured;
    private boolean userOnly;
    private boolean clientOnly;

    private String resourceId;

    private String path;

    private String method;
}
