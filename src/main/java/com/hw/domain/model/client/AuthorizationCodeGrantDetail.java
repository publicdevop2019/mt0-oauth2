package com.hw.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.domain.model.client.event.ClientGrantTypeChanged;
import com.hw.shared.IdGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class AuthorizationCodeGrantDetail {
    public static final GrantType NAME = GrantType.AUTHORIZATION_CODE;
    @Id
    private long id;

    public AuthorizationCodeGrantDetail() {
    }

    //    @Convert(converter = StringSetConverter.class)
    private HashSet<String> redirectUrls = new HashSet<>();
    private boolean autoApprove = false;
    private boolean enabled = false;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client client;

    public AuthorizationCodeGrantDetail(Set<GrantType> grantTypes, Set<String> redirectUrls, boolean autoApprove) {
        this.setRedirectUrls(redirectUrls);
        this.setAutoApprove(autoApprove);
        id = IdGenerator.instance().id();
        enabled = grantTypes.stream().anyMatch(e -> e.equals(NAME));
    }

    public AuthorizationCodeGrantDetail(Set<GrantType> grantTypes, Set<String> redirectUrls, boolean autoApprove, ClientId clientId) {
        this.setRedirectUrls(redirectUrls);
        this.setAutoApprove(autoApprove);
        id = IdGenerator.instance().id();
        enabled = grantTypes.stream().anyMatch(e -> e.equals(NAME));
    }

    public void setAutoApprove(boolean autoApprove) {
        this.autoApprove = autoApprove;
    }

    public Set<String> redirectUrls() {
        return redirectUrls;
    }

    public boolean autoApprove() {
        return autoApprove;
    }

    public void setRedirectUrls(Set<String> redirectUrls) {
        this.redirectUrls = new HashSet<>(redirectUrls);
    }

    public void replace(AuthorizationCodeGrantDetail authorizationCodeGrantDetail) {
        if (grantTypeChanged(authorizationCodeGrantDetail)) {
            DomainEventPublisher.instance().publish(new ClientGrantTypeChanged(client.clientId()));
        }
        this.setAutoApprove(authorizationCodeGrantDetail.autoApprove);
        this.setRedirectUrls(authorizationCodeGrantDetail.redirectUrls);
    }

    private boolean grantTypeChanged(AuthorizationCodeGrantDetail authorizationCodeGrantDetail) {
        return enabled != authorizationCodeGrantDetail.enabled();
    }

    public boolean enabled() {
        return enabled;
    }
}
