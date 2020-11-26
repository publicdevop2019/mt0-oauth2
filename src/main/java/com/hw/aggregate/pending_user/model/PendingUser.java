package com.hw.aggregate.pending_user.model;

import com.hw.aggregate.pending_user.PendingUserRepo;
import com.hw.aggregate.user.AppBizUserApplicationService;
import com.hw.aggregate.user.representation.AppBizUserCardRep;
import com.hw.shared.Auditable;
import com.hw.shared.IdGenerator;
import com.hw.shared.rest.Aggregate;
import com.hw.shared.sql.SumPagedRep;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.Email;

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

    public static PendingUser create(String email, PendingUserRepo pendingRORepo, AppBizUserApplicationService appBizUserApplicationService, IdGenerator idGenerator) {
        validateOnCreate(email, appBizUserApplicationService);
        PendingUser pendingResourceOwner = pendingRORepo.findOneByEmail(email);
        if (pendingResourceOwner == null) {
            pendingResourceOwner = new PendingUser();
            pendingResourceOwner.setEmail(email);
            pendingResourceOwner.setId(idGenerator.getId());
        }
        pendingResourceOwner.setActivationCode(PendingUser.generateCode());
        return pendingResourceOwner;
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
