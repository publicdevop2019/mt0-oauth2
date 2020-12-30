package com.mt.identityaccess.application.user;

import com.mt.identityaccess.domain.model.user.User;
import lombok.Data;

@Data
public class PublicUserCardRepresentation {
    private String id;

    public PublicUserCardRepresentation(User bizUser) {
        this.id = bizUser.getUserId().getDomainId();
    }
}
