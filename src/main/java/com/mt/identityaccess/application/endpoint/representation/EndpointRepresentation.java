package com.mt.identityaccess.application.endpoint.representation;

import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import lombok.Data;

@Data
public class EndpointRepresentation {
    private String id;
    private String expression;
    private String resourceId;
    private String description;
    private String path;
    private String method;
    private String createdBy;
    private Long createdAt;
    private String modifiedBy;
    private Long modifiedAt;
    private Integer version;

    public EndpointRepresentation(Endpoint endpoint) {
        this.id = endpoint.getEndpointId().getDomainId();
        this.expression = endpoint.getExpression();
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
