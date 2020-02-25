package com.hw.clazz;

import com.hw.entity.ResourceOwner;
import lombok.Data;

@Data
public class ResourceOwnerUpdatePwd extends ResourceOwner {
    private String currentPwd;
}
