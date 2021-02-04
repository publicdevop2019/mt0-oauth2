package com.mt.identityaccess.domain.model.pending_user;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.validate.Validator;
import com.mt.identityaccess.domain.DomainRegistry;
import com.mt.identityaccess.domain.model.user.User;
import com.mt.identityaccess.domain.model.user.UserEmail;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Optional;

@NoArgsConstructor
public class RegistrationEmail extends DomainId {
    @Getter
    @Column(unique = true)
    private String email;

    public RegistrationEmail(String email) {
        super(email);
        setEmail(email);
    }

    private void setEmail(String email) {
        Validator.notNull(email);
        Validator.notBlank(email);
        Validator.isEmail(email);
        this.email = email;
    }

    @Override
    public String getDomainId() {
        return this.email;
    }
}
