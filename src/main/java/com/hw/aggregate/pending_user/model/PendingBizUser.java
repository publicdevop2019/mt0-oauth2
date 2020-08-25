package com.hw.aggregate.pending_user.model;

import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.aggregate.pending_user.PendingBizUserRepo;
import com.hw.aggregate.user.BizUserApplicationService;
import com.hw.aggregate.user.BizUserRepo;
import com.hw.aggregate.user.model.BizUser;
import com.hw.aggregate.user.model.BizUserAuthorityEnum;
import com.hw.shared.BadRequestException;
import com.hw.shared.IdGenerator;
import com.hw.shared.sql.SumPagedRep;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Collections;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
@Data
public class PendingBizUser {
    @Id
    private Long id;

    @Email
    @Column
    private String email;

    @Column
    private String activationCode;

    @Column
    private String password;

    public static PendingBizUser create(String email, PendingBizUserRepo pendingRORepo, BizUserApplicationService resourceOwnerRepo, IdGenerator idGenerator) {
        validateOnCreate(email, pendingRORepo, resourceOwnerRepo);
        PendingBizUser pendingResourceOwner = pendingRORepo.findOneByEmail(email);
        if (pendingResourceOwner == null) {
            pendingResourceOwner = new PendingBizUser();
            pendingResourceOwner.setEmail(email);
        }
        pendingResourceOwner.setActivationCode(PendingBizUser.generateCode());
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

    private static void validateOnCreate(String email, PendingBizUserRepo pendingRORepo, BizUserApplicationService bizUserApplicationService) {
        if (!StringUtils.hasText(email))
            throw new BadRequestException("email is empty");
        SumPagedRep<Void> voidSumPagedRep = bizUserApplicationService.readByQuery("email:" + email, null, null);
        BizUser var2 = bizUserApplicationService.findOneByEmail(email);
        if (var2 != null)
            throw new BadRequestException("already an user " + email);
    }

    public BizUser convert(PasswordEncoder encoder, PendingBizUserRepo pendingResourceOwnerRepo, BizUserRepo userRepo, IdGenerator idGenerator) {
        validateOnConvert(pendingResourceOwnerRepo, userRepo);
        BizUser var1 = new BizUser();
        var1.setId(idGenerator.getId());
        var1.setEmail(email);
        var1.setPassword(encoder.encode(password));
        var1.setGrantedAuthorities(Collections.singletonList(new GrantedAuthorityImpl(BizUserAuthorityEnum.ROLE_USER)));
        var1.setLocked(false);
        return userRepo.save(var1);
    }

    private void validateOnConvert(PendingBizUserRepo pendingRORepo, BizUserRepo resourceOwnerRepo) throws BadRequestException {
        if (!StringUtils.hasText(email))
            throw new BadRequestException("email is empty");
        if (!StringUtils.hasText(password))
            throw new BadRequestException("password is empty");
        if (!StringUtils.hasText(activationCode))
            throw new BadRequestException("activationCode is empty");
        BizUser var1 = resourceOwnerRepo.findOneByEmail(email);
        if (var1 != null)
            throw new BadRequestException("already an user " + email);
        PendingBizUser var2 = pendingRORepo.findOneByEmail(email);
        if (var2 == null)
            throw new BadRequestException("please get activation code first");
        if (!var2.getActivationCode().equals(activationCode))
            throw new BadRequestException("activation code mismatch");
    }
}
