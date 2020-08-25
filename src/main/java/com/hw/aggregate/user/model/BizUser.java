package com.hw.aggregate.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.aggregate.client.model.GrantedAuthorityImpl;
import com.hw.shared.Auditable;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

/**
 * use same sequence generator for resourceOwner & client, can use
 * different sequence since as long as blacklist use to table for client and resource owner
 */
@Entity
@Table
@Data
public class BizUser extends Auditable implements UserDetails {
    @Id
    protected Long id;

    @Column(nullable = false)
    @NotBlank
    @Email
    private String email;

    @Column(nullable = false)
    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    @NotNull
    private Boolean locked;

    @Column(nullable = false)
    @NotNull
    @NotEmpty
    @Convert(converter = BizUserAuthorityEnum.ResourceOwnerAuthorityConverter.class)
    private List<@Valid @NotNull GrantedAuthorityImpl<BizUserAuthorityEnum>> grantedAuthorities;

    @Column
    private boolean subscription;

    /**
     * make sure grantedAuthorities only get serialized once
     */
    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return password;
    }


    @Override
    public String getUsername() {
        return id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return locked == null || !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}
