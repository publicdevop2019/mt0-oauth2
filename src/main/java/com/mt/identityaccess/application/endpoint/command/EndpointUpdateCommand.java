package com.mt.identityaccess.application.endpoint.command;

import com.mt.common.rest.AggregateUpdateCommand;
import lombok.Data;

import java.io.Serializable;

@Data
public class EndpointUpdateCommand implements Serializable, AggregateUpdateCommand {
    private static final long serialVersionUID = 1;
    private String expression;
    private String description;

    private String resourceId;

    private String path;

    private String method;
    private Integer version;
}
