package com.hw.aggregate.user.representation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
@Data
public class AdminBizUserRep {
    private Long id;

    private String email;
    private Boolean locked;
    private List<GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities;
    private String createdBy;

    private Long createdAt;

    private String modifiedBy;

    private Long modifiedAt;
    public AdminBizUserRep(BizUser bizUser) {
        this.id=bizUser.getId();
        this.email= bizUser.getEmail();
        this.locked=bizUser.getLocked();
        this.grantedAuthorities=bizUser.getGrantedAuthorities();
        this.createdAt=bizUser.getCreatedAt().getTime();
        this.modifiedAt=bizUser.getModifiedAt().getTime();
        this.createdBy=bizUser.getCreatedBy();
        this.modifiedBy=bizUser.getModifiedBy();
    }
}
