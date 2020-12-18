package com.mt.identityaccess.infrastructure;

import com.mt.identityaccess.config.DomainEvent;
import org.springframework.data.repository.CrudRepository;

public interface EventStoreRepository extends CrudRepository<DomainEvent,Long> {
}
