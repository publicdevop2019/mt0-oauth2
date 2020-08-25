package com.hw.aggregate.user.command;

import com.hw.aggregate.user.model.BizUser;
import lombok.Data;

@Data
public class BizBizUserUpdatePwd extends BizUser {
    private String currentPwd;
}
