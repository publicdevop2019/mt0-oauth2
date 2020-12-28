package com.mt.identityaccess.domain.model.pending_user;

import com.mt.common.domain.model.id.DomainId;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Optional;
@NoArgsConstructor
public class RegistrationEmail extends DomainId {
    @Getter
    @Setter(AccessLevel.PRIVATE)
    @Column(unique = true)
    @Email
    private String email;

    public RegistrationEmail(@NotNull String email) {
        if (!StringUtils.hasText(email))
            throw new IllegalArgumentException("email is empty");
        Optional<User> user = DomainRegistry.userRepository().searchExistingUserWith(email);
        if (user.isPresent())
            throw new IllegalArgumentException("already an user " + email);
        setEmail(email);
    }

    @Override
    public String getDomainId() {
        return this.email;
    }
}
