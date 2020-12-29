package com.mt.identityaccess.application.user;

import com.mt.identityaccess.domain.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppUserCardRepresentation {
    private String id;

    private String email;

    public AppUserCardRepresentation(Object o) {
        User o1 = (User) o;
        id = o1.getUserId().getDomainId();
        email = o1.getEmail().getEmail();
    }
}
