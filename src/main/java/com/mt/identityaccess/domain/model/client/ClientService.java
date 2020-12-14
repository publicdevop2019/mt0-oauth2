package com.mt.identityaccess.domain.model.client;

import com.hw.config.DomainEventPublisher;
import com.hw.shared.sql.SumPagedRep;
import com.mt.identityaccess.domain.model.DomainRegistry;
import com.mt.identityaccess.domain.model.client.event.ClientProvisioned;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService {

    public ClientId provisionClient(BasicClientDetail basicClientDetail,
                                    ClientCredentialsGrantDetail clientCredentialsGrantDetail,
                                    PasswordGrantDetail passwordGrantDetail,
                                    RefreshTokenGrantDetail refreshTokenGrantDetail,
                                    AuthorizationCodeGrantDetail authorizationCodeGrantDetail,
                                    AccessTokenDetail accessTokenDetail
                                    ) {
        ClientId clientId = DomainRegistry.clientRepository().nextIdentity();
        Client client = new Client(clientId,
                basicClientDetail, clientCredentialsGrantDetail, passwordGrantDetail, refreshTokenGrantDetail, authorizationCodeGrantDetail,accessTokenDetail);
        DomainRegistry.clientRepository().add(client);
        DomainEventPublisher.instance().publish(new ClientProvisioned(client.clientId()));
        return clientId;
    }

    public List<Client> getClientsOfQuery(ClientQuery queryParam) {
        ClientPaging queryPagingParam = new ClientPaging();
        SumPagedRep<Client> tSumPagedRep = DomainRegistry.clientRepository().clientsOfQuery(queryParam, queryPagingParam);
        if (tSumPagedRep.getData().size() == 0)
            return new ArrayList<>();
        double l = (double) tSumPagedRep.getTotalItemCount() / tSumPagedRep.getData().size();//for accuracy
        double ceil = Math.ceil(l);
        int i = BigDecimal.valueOf(ceil).intValue();
        List<Client> data = new ArrayList<>(tSumPagedRep.getData());
        for (int a = 1; a < i; a++) {
            data.addAll(DomainRegistry.clientRepository().clientsOfQuery(queryParam, queryPagingParam.nextPage()).getData());
        }
        return data;
    }
}
