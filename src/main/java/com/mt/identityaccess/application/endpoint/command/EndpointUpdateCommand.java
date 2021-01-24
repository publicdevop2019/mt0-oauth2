package com.mt.identityaccess.application.endpoint.command;

import com.mt.common.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

@Data
public class EndpointUpdateCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private String description;
    private Set<String> clientRoles;
    private Set<String> userRoles;
    private Set<String> scopes;
    private boolean secured;
    private boolean userOnly;
    private boolean clientOnly;
    private String resourceId;

    private String path;

    private String method;
    private Integer version;
}
