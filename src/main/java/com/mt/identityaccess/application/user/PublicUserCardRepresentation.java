package com.mt.identityaccess.application.user;

import com.mt.identityaccess.domain.model.user.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class PublicUserCardRepresentation {
    private Long id;

    public PublicUserCardRepresentation(User bizUser) {
        BeanUtils.copyProperties(bizUser, this);
    }
}
