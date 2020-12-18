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

import com.mt.identityaccess.domain.model.DomainRegistry;
import com.mt.identityaccess.domain.model.client.ClientId;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
public abstract class DomainEvent {
    private static final int version = 0;
    @Id
    private Long id = DomainRegistry.uniqueIdGeneratorService().id();

    private Date occurredOn = new Date();

    @Embedded
    private ClientId clientId;
    @ElementCollection(fetch = FetchType.LAZY)
    @Embedded
    @CollectionTable(
            name = "domain_event_client_ids_map",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id")
    )
    private Set<ClientId> clientIds;

    public DomainEvent(ClientId clientId) {
        this.clientId = clientId;
    }

    public DomainEvent(Set<ClientId> clientIds) {
        this.clientIds = clientIds;
    }

    public int eventVersion() {
        return version;
    }

    public Date occurredOn() {
        return occurredOn;
    }

}
