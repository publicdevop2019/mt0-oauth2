//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.mt.identityaccess.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.mt.identityaccess.domain.model.DomainRegistry;
import com.mt.identityaccess.domain.model.client.ClientId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PRIVATE)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public abstract class DomainEvent implements Serializable {

    @Id
    private Long id;

    private Long timestamp;

    @Embedded
    @AttributeOverride(name = "clientId", column = @Column(updatable = false))
    private ClientId clientId;

    @ElementCollection(fetch = FetchType.LAZY)
    @Embedded
    @CollectionTable(
            name = "domain_event_client_ids_map",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id")
    )
    private Set<ClientId> clientIds;

    public DomainEvent(ClientId clientId) {
        setId(DomainRegistry.uniqueIdGeneratorService().id());
        setTimestamp(new Date().getTime());
        setClientId(clientId);
    }

    public DomainEvent(Set<ClientId> clientIds) {
        setId(DomainRegistry.uniqueIdGeneratorService().id());
        setTimestamp(new Date().getTime());
        setClientIds(clientIds);
    }
}
