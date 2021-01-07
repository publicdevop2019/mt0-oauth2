package com.mt.identityaccess.application.endpoint.command;

import com.mt.common.rest.TypedClass;
import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import lombok.Data;

@Data
public class EndpointPatchCommand extends TypedClass<EndpointPatchCommand> {
    private String expression;
    private String description;
    private String resourceId;
    private String path;
    private String method;

    public EndpointPatchCommand(Endpoint bizEndpoint) {
        super(EndpointPatchCommand.class);
        this.expression = bizEndpoint.getExpression();
        this.description = bizEndpoint.getDescription();
        this.resourceId = bizEndpoint.getClientId().getDomainId();
        this.path = bizEndpoint.getPath();
        this.method = bizEndpoint.getMethod();
    }

    public EndpointPatchCommand() {
        super(EndpointPatchCommand.class);
    }

}
