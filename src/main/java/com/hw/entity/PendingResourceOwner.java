package com.hw.entity;

import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.hw.repo.PendingResourceOwnerRepo;
import com.hw.repo.ResourceOwnerRepo;
import com.hw.shared.BadRequestException;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Collections;

@Entity
@Table
@SequenceGenerator(name = "pendingROId_gen", sequenceName = "pendingROId_gen", initialValue = 100)
@Data
public class PendingResourceOwner {
    @Id
    @GeneratedValue(generator = "pendingROId_gen")
    private Long id;

    @Email
    @Column
    private String email;

    @Column
    private String activationCode;

    @Column
    private String password;

    public static PendingResourceOwner create(String email, PendingResourceOwnerRepo pendingRORepo, ResourceOwnerRepo resourceOwnerRepo) {
        validateOnCreate(email, pendingRORepo, resourceOwnerRepo);
        PendingResourceOwner pendingResourceOwner = pendingRORepo.findOneByEmail(email);
        if (pendingResourceOwner == null) {
            pendingResourceOwner = new PendingResourceOwner();
            pendingResourceOwner.setEmail(email);
        }
        pendingResourceOwner.setActivationCode(PendingResourceOwner.generateCode());
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

    public ResourceOwner convert(PasswordEncoder encoder, PendingResourceOwnerRepo pendingResourceOwnerRepo, ResourceOwnerRepo userRepo) {
        validateOnConvert(pendingResourceOwnerRepo, userRepo);
        ResourceOwner var1 = new ResourceOwner();
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
