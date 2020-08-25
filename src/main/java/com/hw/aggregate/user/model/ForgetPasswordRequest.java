package com.hw.aggregate.user.model;

import com.hw.aggregate.user.BizUserRepo;
import com.hw.aggregate.user.ForgetPasswordRequestRepo;
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


}
