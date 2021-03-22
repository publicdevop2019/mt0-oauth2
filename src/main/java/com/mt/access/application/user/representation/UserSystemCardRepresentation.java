package com.mt.access.application.user.representation;

import com.mt.access.domain.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSystemCardRepresentation {
    private String id;

    private String email;

    public UserSystemCardRepresentation(Object o) {
        User o1 = (User) o;
        id = o1.getUserId().getDomainId();
        email = o1.getEmail().getEmail();
    }
}
