package com.hw.aggregate.user.model;

import com.hw.aggregate.user.ForgetPasswordRequestRepo;
import com.hw.aggregate.user.BizUserRepo;
import com.hw.shared.Auditable;
import com.hw.shared.BadRequestException;
import com.hw.shared.IdGenerator;
import lombok.Data;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
@Data
public class ForgetPasswordRequest extends Auditable {

    @Id
    private Long id;

    @Email
    @Column
    private String email;

    @Column
    private String token;

    @Column
    private String newPassword;

    @Column
    private Boolean consumed;

    public static ForgetPasswordRequest create(String email, ForgetPasswordRequestRepo forgetPasswordRequestRepo, BizUserRepo resourceOwnerRepo, IdGenerator idGenerator) {
        validateOnCreate(email, resourceOwnerRepo);
        ForgetPasswordRequest oneByEmail = forgetPasswordRequestRepo.findOneByEmail(email);
        if (oneByEmail == null) {
            oneByEmail = new ForgetPasswordRequest();
            oneByEmail.setEmail(email);
            oneByEmail.setId(idGenerator.getId());
        }
        oneByEmail.setToken(generateToken());
        oneByEmail.setConsumed(Boolean.FALSE);
        forgetPasswordRequestRepo.save(oneByEmail);
        return oneByEmail;
    }

    private static void validateOnCreate(String email, BizUserRepo resourceOwnerRepo) throws BadRequestException {
        if (!StringUtils.hasText(email))
            throw new BadRequestException("empty email");
        BizUser oneByEmail = resourceOwnerRepo.findOneByEmail(email);
        if (oneByEmail == null)
            throw new BadRequestException("user does not exist");
    }

    private static String generateToken() {
        return "123456789";
//        return UUID.randomUUID().toString().replace("-", "");
    }

    public void verifyToken(ForgetPasswordRequestRepo forgetPasswordRequestRepo, BizUserRepo resourceOwnerRepo) throws BadRequestException {
        if (!StringUtils.hasText(email))
            throw new BadRequestException("empty email");
        if (!StringUtils.hasText(token))
            throw new BadRequestException("empty token");
        if (!StringUtils.hasText(newPassword))
            throw new BadRequestException("empty new password");
        ForgetPasswordRequest oneByEmail = forgetPasswordRequestRepo.findOneByEmail(email);
        if (oneByEmail == null)
            throw new BadRequestException("no forget password request found");
        BizUser oneByEmail2 = resourceOwnerRepo.findOneByEmail(email);
        if (oneByEmail2 == null)
            throw new BadRequestException("user does not exist");
        if (!oneByEmail.getToken().equals(token))
            throw new BadRequestException("token mismatch");
        if (oneByEmail.getConsumed())
            throw new BadRequestException("token already used");
        oneByEmail.setConsumed(Boolean.TRUE);
        forgetPasswordRequestRepo.save(oneByEmail);
    }
}
