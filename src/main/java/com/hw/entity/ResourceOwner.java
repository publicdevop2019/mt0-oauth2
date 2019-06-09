package com.hw.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hw.clazz.GrantedAuthorityImpl;
import com.hw.converter.GrantedAuthorityConverter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "resource_owner")
@SequenceGenerator(name = "userId_gen", sequenceName = "userId_gen", initialValue = 100)
public class ResourceOwner extends Auditable implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "userId_gen")
    @Column(nullable = false)
    protected Long id;

    @Column(nullable = false)
    @NotBlank
    private String email;

    @Column(nullable = false)
    @NotEmpty
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    @JsonProperty //NOTE deserialization fix
    @NotNull
    private Boolean locked;

    @Column(nullable = false)
    @NotNull
    @NotEmpty
    @Convert(converter = GrantedAuthorityConverter.class)
    private Collection<@Valid @NotNull GrantedAuthorityImpl> grantedAuthority;

    @Column
    private String resourceId;

    //NOTE: required by JPA
    public ResourceOwner() {
    }

    public ResourceOwner(String username, String pwd) {
        setEmail(username);
        setPassword(pwd);
    }

    public ResourceOwner setLocked(boolean locked) {
        this.locked = locked;
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthority;
    }

    public ResourceOwner setGrantedAuthority(List<GrantedAuthorityImpl> grantedAuthority) {
        this.grantedAuthority = grantedAuthority;
        return this;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public ResourceOwner setPassword(String password) {
        this.password = password;
        return this;
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
        return !locked;
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
