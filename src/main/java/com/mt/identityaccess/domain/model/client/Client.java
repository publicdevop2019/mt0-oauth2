package com.mt.identityaccess.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.shared.Auditable;
import com.hw.shared.rest.Aggregate;
import com.mt.identityaccess.domain.model.client.event.ClientReplaced;
import com.mt.identityaccess.domain.model.client.grant.*;
import lombok.Setter;
import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * use different field name to make it more flexible also avoid copy properties type mismatch
 * e.g getting return string instead of enum
 */
@Entity
@Table
@Where(clause = "deleted = false")
@Setter
public class Client extends Auditable implements Aggregate {
    private ClientId clientId;
    private BasicClientDetail basicClientDetail;
    private ClientCredentialsGrantDetail clientCredentialsGrantDetail;
    private PasswordGrantDetail passwordGrantDetail;
    private AuthorizationCodeGrantDetail authorizationCodeGrantDetail;
    private RefreshTokenGrantDetail refreshTokenGrantDetail;
    private AccessTokenDetail accessTokenDetail;

    public BasicClientDetail basicClientDetail() {
        return basicClientDetail;
    }

    public Client() {
    }

    public Client(ClientId nextIdentity,
                  BasicClientDetail basicClientDetail,
                  ClientCredentialsGrantDetail clientCredentialsGrantDetail,
                  PasswordGrantDetail passwordGrantDetail,
                  RefreshTokenGrantDetail refreshTokenGrantDetail,
                  AuthorizationCodeGrantDetail authorizationCodeGrantDetail,
                  AccessTokenDetail accessTokenDetail
    ) {

        this.setClientId(nextIdentity);
        this.setBasicClientDetail(basicClientDetail);
        this.setClientCredentialsGrantDetail(clientCredentialsGrantDetail);
        this.setPasswordGrantDetail(passwordGrantDetail);
        this.setRefreshTokenGrantDetail(refreshTokenGrantDetail);
        this.setAuthorizationCodeGrantDetail(authorizationCodeGrantDetail);
        this.setAccessTokenDetail(accessTokenDetail);
    }

    public Set<GrantType> totalGrantTypes() {
        HashSet<GrantType> grantTypes = new HashSet<>();
        if (clientCredentialsGrantDetail != null) {
            grantTypes.add(clientCredentialsGrantDetail.getGrantType());
        }
        if (passwordGrantDetail != null) {
            grantTypes.add(passwordGrantDetail.getGrantType());
        }
        if (authorizationCodeGrantDetail != null) {
            grantTypes.add(authorizationCodeGrantDetail.getGrantType());
        }
        if (refreshTokenGrantDetail != null) {
            grantTypes.add(refreshTokenGrantDetail.getGrantType());
        }
        return grantTypes;
    }

    public AccessTokenDetail accessTokenDetail() {
        return accessTokenDetail;
    }

    public AuthorizationCodeGrantDetail authorizationCodeGrantDetail() {
        return authorizationCodeGrantDetail;
    }

    public RefreshTokenGrantDetail refreshTokenGrantDetail() {
        return refreshTokenGrantDetail;
    }

    public ClientId clientId() {
        return clientId;
    }

    public void replace(
            BasicClientDetail basicClientDetail,
            ClientCredentialsGrantDetail clientCredentialsGrantDetail,
            PasswordGrantDetail passwordGrantDetail,
            RefreshTokenGrantDetail refreshTokenGrantDetail,
            AuthorizationCodeGrantDetail authorizationCodeGrantDetail,
            AccessTokenDetail accessTokenDetail
    ) {
        this.basicClientDetail.replace(basicClientDetail);
        this.clientCredentialsGrantDetail.replace(clientCredentialsGrantDetail);
        this.passwordGrantDetail.replace(passwordGrantDetail);
        this.refreshTokenGrantDetail.replace(refreshTokenGrantDetail);
        this.authorizationCodeGrantDetail.replace(authorizationCodeGrantDetail);
        this.accessTokenDetail.replace(accessTokenDetail);
        DomainEventPublisher.instance().publish(new ClientReplaced(clientId()));
    }

    public void replace(BasicClientDetail basicClientDetail,
                        ClientCredentialsGrantDetail clientCredentialsGrantDetail,
                        PasswordGrantDetail passwordGrantDetail,
                        AccessTokenDetail accessTokenDetail
    ) {
        this.basicClientDetail.replace(basicClientDetail);
        this.clientCredentialsGrantDetail.replace(clientCredentialsGrantDetail);
        this.passwordGrantDetail.replace(passwordGrantDetail);
        this.accessTokenDetail.replace(accessTokenDetail);
        DomainEventPublisher.instance().publish(new ClientReplaced(clientId()));
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public Integer getVersion() {
        return null;
    }
}
