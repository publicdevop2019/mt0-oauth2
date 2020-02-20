package com.hw.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.clazz.eenum.ResourceOwnerAuthorityEnum;
import com.hw.converter.StringListConverter;
import com.hw.shared.Auditable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
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
import java.util.Set;

@Entity
@Table(name = "resource_owner")
@Data
@SequenceGenerator(name = "entityId_gen", sequenceName = "entityId_seq", initialValue = 100)
public class ResourceOwner extends Auditable implements UserDetails {
    /**
     * use same sequence generator for resourceOwner & client to avoid blacklist conflict
     * e.g client has same id as resource owner
     */
    @Id
    @GeneratedValue(generator = "entityId_gen")
    @Setter(AccessLevel.NONE)
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
    @Convert(converter = ResourceOwnerAuthorityEnum.ResourceOwnerAuthorityConverter.class)
    private List<@Valid @NotNull GrantedAuthorityImpl<ResourceOwnerAuthorityEnum>> grantedAuthorities;

    @Column
    @Convert(converter = StringListConverter.class)
    private Set<String> resourceId;

    /**
     * make sure grantedAuthorities only get serialized once
     *
     * @return
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
        return email;
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
