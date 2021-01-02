package com.mt.identityaccess.application.client.representation;

import com.mt.identityaccess.domain.model.endpoint.Endpoint;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class EndpointRepresentation {
    private String expression;
    private String resourceId;
    private String description;
    private String path;
    private String method;
    private Long id;
    private String createdBy;
    private Long createdAt;
    private String modifiedBy;
    private Long modifiedAt;
    private Integer version;

    public EndpointRepresentation(Endpoint endpoint) {
        BeanUtils.copyProperties(endpoint, this);
        this.createdAt = endpoint.getCreatedAt() != null ? endpoint.getCreatedAt().getTime() : null;
        this.modifiedAt = endpoint.getModifiedAt() != null ? endpoint.getModifiedAt().getTime() : null;
    }
}
