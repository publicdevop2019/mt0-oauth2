package com.mt.identityaccess.domain.model.pending_user;

import com.mt.common.Auditable;
import com.mt.common.rest.Aggregate;
import com.mt.common.snowflake.IdGenerator;
import com.mt.common.sql.SumPagedRep;
import com.mt.identityaccess.application.deprecated.AppBizUserApplicationService;
import com.mt.identityaccess.application.representation.AppBizUserCardRep;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Optional;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"email"}))
@Data
public class PendingUser extends Auditable implements Aggregate {
    @Id
    private Long id;

    @Email
    @Column
    private String email;

    @Column
    private String activationCode;

    @Version
    @Setter(AccessLevel.NONE)
    private Integer version;

    public static PendingUser create(String email, PendingUserRepository pendingRORepo, AppBizUserApplicationService appBizUserApplicationService, IdGenerator idGenerator) {
        validateOnCreate(email, appBizUserApplicationService);
        Optional<PendingUser> pendingResourceOwner = pendingRORepo.registeredUsing(email);
        if (pendingResourceOwner.isEmpty()) {
            PendingUser pendingUser = new PendingUser();
            pendingUser.setEmail(email);
            pendingUser.setId(idGenerator.id());
            pendingUser.setActivationCode(PendingUser.generateCode());
            return pendingUser;
        } else {
            pendingResourceOwner.get().setActivationCode(PendingUser.generateCode());
            return pendingResourceOwner.get();
        }
    }

    private static String generateCode() {
        return "123456";// for testing
//        int m = (int) Math.pow(10, 6 - 1);
//        return String.valueOf(m + new Random().nextInt(9 * m));
    }

    private static void validateOnCreate(String email, AppBizUserApplicationService bizUserApplicationService) {
        if (!StringUtils.hasText(email))
            throw new IllegalArgumentException("email is empty");
        SumPagedRep<AppBizUserCardRep> appBizUserCardRepSumPagedRep = bizUserApplicationService.readByQuery("email:" + email, null, null);
        if (!appBizUserCardRepSumPagedRep.getData().isEmpty())
            throw new IllegalArgumentException("already an user " + email);
    }


}
