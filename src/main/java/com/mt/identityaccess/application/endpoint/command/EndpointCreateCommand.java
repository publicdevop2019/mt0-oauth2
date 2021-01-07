package com.mt.identityaccess.application.endpoint.command;

import lombok.Data;

import java.io.Serializable;

@Data
public class EndpointCreateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String expression;
    private String description;

    private String resourceId;

    private String path;

    private String method;
}
