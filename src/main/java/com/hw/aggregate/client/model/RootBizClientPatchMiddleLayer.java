package com.hw.aggregate.client.model;

import com.hw.shared.rest.TypedClass;
import lombok.Data;

@Data
public class RootBizClientPatchMiddleLayer extends TypedClass<RootBizClientPatchMiddleLayer> {
    private String description;
    private String name;
    public RootBizClientPatchMiddleLayer(BizClient bizClient) {
        super(RootBizClientPatchMiddleLayer.class);
        this.description = bizClient.getDescription();
        this.name = bizClient.getName();
    }

    public RootBizClientPatchMiddleLayer() {
        super(RootBizClientPatchMiddleLayer.class);
    }
}
