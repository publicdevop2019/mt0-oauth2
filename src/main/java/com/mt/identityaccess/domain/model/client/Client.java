package com.mt.identityaccess.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.shared.Auditable;
import com.hw.shared.rest.Aggregate;
import com.mt.identityaccess.domain.model.client.event.ClientReplaced;
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
//    public void shouldRevoke(ReplaceClientCommand newClient, RevokeTokenService tokenRevocationService) {

//        if (StringUtils.hasText(newClient.getClientSecret())
//                || authorityChanged(this, newClient)
//                || scopeChanged(this, newClient)
//                || accessTokenChanged(this, newClient)
//                || refreshTokenChanged(this, newClient)
//                || grantTypeChanged(this, newClient)
//                || resourceIdChanged(this, newClient)
//                || redirectUrlChanged(this, newClient)
//
//        ) {
//            tokenRevocationService.revokeClientToken(getClientId());
//        }
//    }

//    private boolean authorityChanged(Client oldClient, ReplaceClientCommand newClient) {
//        return !oldClient.getGrantedAuthorities().equals(newClient.getGrantedAuthorities());
//    }
//
//    private boolean scopeChanged(Client oldClient, ReplaceClientCommand newClient) {
//        return !oldClient.getScopeEnums().equals(newClient.getScopeEnums());
//    }

//    private boolean accessTokenChanged(Client oldClient, ReplaceClientCommand newClient) {
//        return !oldClient.getAccessTokenValiditySeconds().equals(newClient.getAccessTokenValiditySeconds());
//    }
//
//    private boolean refreshTokenChanged(Client oldClient, ReplaceClientCommand newClient) {
//        if (oldClient.getRefreshTokenValiditySeconds() == null && newClient.getRefreshTokenValiditySeconds() == null) {
//            return false;
//        } else if (oldClient.getRefreshTokenValiditySeconds() != null && oldClient.getRefreshTokenValiditySeconds().equals(newClient.getRefreshTokenValiditySeconds())) {
//            return false;
//        } else
//            return newClient.getRefreshTokenValiditySeconds() == null || !newClient.getRefreshTokenValiditySeconds().equals(oldClient.getRefreshTokenValiditySeconds());
//    }
//
//    private boolean grantTypeChanged(Client oldClient, ReplaceClientCommand newClient) {
//        return !oldClient.getGrantTypeEnums().equals(newClient.getGrantTypeEnums());
//    }
//
//    private boolean redirectUrlChanged(Client oldClient, ReplaceClientCommand newClient) {
//        if ((oldClient.getRegisteredRedirectUri() == null || oldClient.getRegisteredRedirectUri().isEmpty())
//                && (newClient.getRegisteredRedirectUri() == null || newClient.getRegisteredRedirectUri().isEmpty())) {
//            return false;
//        } else if (oldClient.getRegisteredRedirectUri() != null && oldClient.getRegisteredRedirectUri().equals(newClient.getRegisteredRedirectUri())) {
//            return false;
//        } else
//            return newClient.getRegisteredRedirectUri() == null || !newClient.getRegisteredRedirectUri().equals(oldClient.getRegisteredRedirectUri());
//    }
//
//    private boolean resourceIdChanged(Client oldClient, ReplaceClientCommand newClient) {
//        return !oldClient.getFollowing().stream().map(e -> e.getId().toString()).collect(Collectors.toSet()).equals(newClient.getResourceIds());
//    }


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
        DomainEventPublisher.instance().publish(new ClientReplaced(clientId()));
    }

    public void replace(BasicClientDetail basicClientDetail,
                        ClientCredentialsGrantDetail clientCredentialsGrantDetail,
                        PasswordGrantDetail passwordGrantDetail,
                        AccessTokenDetail accessTokenDetail
    ) {
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
