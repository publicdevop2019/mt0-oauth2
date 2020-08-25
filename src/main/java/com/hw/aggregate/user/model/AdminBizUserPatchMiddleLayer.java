package com.hw.aggregate.user.model;

import com.hw.aggregate.client.model.BizClient;
import com.hw.aggregate.client.model.RootBizClientPatchMiddleLayer;
import com.hw.shared.rest.TypedClass;

public class AdminBizUserPatchMiddleLayer  extends TypedClass<AdminBizUserPatchMiddleLayer> {

    public AdminBizUserPatchMiddleLayer(BizUser bizUser) {
        super(AdminBizUserPatchMiddleLayer.class);
    }

    public AdminBizUserPatchMiddleLayer() {
        super(AdminBizUserPatchMiddleLayer.class);
    }
}
