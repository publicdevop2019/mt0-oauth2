package com.mt.identityaccess.domain.model.client;

import com.hw.config.DomainEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;

public class ClientProvisioningService {
    @Autowired
    private ClientRepository clientRepository;

    public ClientId provisionClient(BasicClientDetail basicClientDetail,
                                    ClientCredentialsGrantDetail clientCredentialsGrantDetail,
                                    PasswordGrantDetail passwordGrantDetail,
                                    RefreshTokenGrantDetail refreshTokenGrantDetail,
                                    AuthorizationCodeGrantDetail authorizationCodeGrantDetail) {
        ClientId clientId = clientRepository.nextIdentity();
        Client client = new Client(clientId,
                basicClientDetail, clientCredentialsGrantDetail, passwordGrantDetail, refreshTokenGrantDetail,authorizationCodeGrantDetail);
        clientRepository.save(client);
        DomainEventPublisher.instance().publish(new ClientProvisioned(client.clientId()));
        return clientId;
    }
}
