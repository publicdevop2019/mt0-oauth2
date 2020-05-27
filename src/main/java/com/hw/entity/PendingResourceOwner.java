package com.hw.entity;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.hw.repo.PendingResourceOwnerRepo;
import com.hw.repo.ResourceOwnerRepo;
import com.hw.shared.BadRequestException;
import com.hw.shared.IdGenerator;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import java.util.Collections;

@Entity
@Table
@Data
public class PendingResourceOwner {
    @Id
    private Long id;

    @Email
    @Column
    private String email;

    @Column
    private String activationCode;

    @Column
    private String password;

    public static PendingResourceOwner create(String email, PendingResourceOwnerRepo pendingRORepo, ResourceOwnerRepo resourceOwnerRepo, IdGenerator idGenerator) {
        validateOnCreate(email, pendingRORepo, resourceOwnerRepo);
        PendingResourceOwner pendingResourceOwner = pendingRORepo.findOneByEmail(email);
        if (pendingResourceOwner == null) {
            pendingResourceOwner = new PendingResourceOwner();
            pendingResourceOwner.setEmail(email);
        }
        pendingResourceOwner.setActivationCode(PendingResourceOwner.generateCode());
        pendingResourceOwner.setId(idGenerator.getId());
        pendingRORepo.save(pendingResourceOwner);
        return pendingResourceOwner;
    }

    // for testing
    private static String generateCode() {
        return "123456";
//        int m = (int) Math.pow(10, 6 - 1);
//        return String.valueOf(m + new Random().nextInt(9 * m));
    }

    private static void validateOnCreate(String email, PendingResourceOwnerRepo pendingRORepo, ResourceOwnerRepo resourceOwnerRepo) {
        if (!StringUtils.hasText(email))
            throw new BadRequestException("email is empty");
        ResourceOwner var2 = resourceOwnerRepo.findOneByEmail(email);
        if (var2 != null)
            throw new BadRequestException("already an user " + email);
    }

    public ResourceOwner convert(PasswordEncoder encoder, PendingResourceOwnerRepo pendingResourceOwnerRepo, ResourceOwnerRepo userRepo, IdGenerator idGenerator) {
        validateOnConvert(pendingResourceOwnerRepo, userRepo);
        ResourceOwner var1 = new ResourceOwner();
        var1.setId(idGenerator.getId());
        var1.setEmail(email);
        var1.setPassword(encoder.encode(password));
        var1.setGrantedAuthorities(Collections.singletonList(new GrantedAuthorityImpl(ResourceOwnerAuthorityEnum.ROLE_USER)));
        var1.setLocked(false);
        return userRepo.save(var1);
    }

    private void validateOnConvert(PendingResourceOwnerRepo pendingRORepo, ResourceOwnerRepo resourceOwnerRepo) throws BadRequestException {
        if (!StringUtils.hasText(email))
            throw new BadRequestException("email is empty");
        if (!StringUtils.hasText(password))
            throw new BadRequestException("password is empty");
        if (!StringUtils.hasText(activationCode))
            throw new BadRequestException("activationCode is empty");
        ResourceOwner var1 = resourceOwnerRepo.findOneByEmail(email);
        if (var1 != null)
            throw new BadRequestException("already an user " + email);
        PendingResourceOwner var2 = pendingRORepo.findOneByEmail(email);
        if (var2 == null)
            throw new BadRequestException("please get activation code first");
        if (!var2.getActivationCode().equals(activationCode))
            throw new BadRequestException("activation code mismatch");
    }
}
