package com.hw.aggregate.user.command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
@Data
public class AdminUpdateBizUserCommand {

    private Boolean locked;

    private List<GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities;

    private boolean subscription;
    private String authorization;
}
