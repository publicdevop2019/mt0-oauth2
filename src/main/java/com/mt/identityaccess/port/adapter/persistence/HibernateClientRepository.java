package com.mt.identityaccess.port.adapter.persistence;

import com.hw.shared.IdGenerator;
import com.hw.shared.sql.SumPagedRep;
import com.hw.shared.sql.builder.SelectQueryBuilder;
import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.client.ClientRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HibernateClientRepository extends JpaRepository<Client, Long>, ClientRepository {
    default ClientId nextIdentity() {
        return new ClientId(IdGenerator.instance().id());
    }

    default Optional<Client> clientOfId(ClientId clientId) {
        return findById(clientId.persistentId());
    }

    default void add(Client client) {
        save(client);
    }

    default SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, ClientPaging clientPaging, QueryConfig queryConfig) {
        return getSumPagedRep(clientQuery.value, clientPaging.value, queryConfig.value);
    }

    default SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery, ClientPaging clientPaging) {
        return getSumPagedRep(clientQuery.value, clientPaging.value, null);
    }

    private SumPagedRep<Client> getSumPagedRep(String query, String page, String config) {
        SelectQueryBuilder<Client> selectQueryBuilder = ClientSelectQueryBuilder.instance();
        List<Client> select = selectQueryBuilder.select(query, page, Client.class);
        Long aLong = null;
        if (!skipCount(config)) {
            aLong = selectQueryBuilder.selectCount(query, Client.class);
        }
        return new SumPagedRep<>(select, aLong);
    }

    private boolean skipCount(String config) {
        return config != null && config.contains("sc:1");
    }
}
