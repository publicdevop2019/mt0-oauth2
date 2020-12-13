package com.mt.identityaccess.infrastructure.persistence;

import com.mt.identityaccess.domain.model.client.Client;
import com.mt.identityaccess.domain.model.client.ClientId;
import com.mt.identityaccess.domain.model.client.ClientRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface HibernateClientRepository extends JpaRepository<Client, Long>, ClientRepository {
    default ClientId nextIdentity() {
        return new ClientId();
    }

    default Optional<Client> clientOfId(ClientId clientId) {
        return findById(clientId.persistentId());
    }

    default void save(Client client) {
        save(client);
    }
}
