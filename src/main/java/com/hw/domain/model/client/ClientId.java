package com.hw.domain.model.client;

import javax.persistence.AttributeConverter;
import javax.persistence.Column;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientId {
    @Column(name = "client_id_id")
    private String clientId;

    public ClientId(String id) {
        this.clientId = id;
    }

    public ClientId() {
    }

    public ClientId(long id) {
        this.clientId = String.valueOf(id);
    }

    public String id() {
        return clientId;
    }

    public static class Converter implements AttributeConverter<Set<ClientId>, String> {
        @Override
        public String convertToDatabaseColumn(Set<ClientId> clientIds) {
            return String.join(",", clientIds.stream().map(ClientId::id).collect(Collectors.toSet()));
        }

        @Override
        public Set<ClientId> convertToEntityAttribute(String s) {
            return Arrays.stream(s.split(",")).map(ClientId::new).collect(Collectors.toSet());
        }
    }
}
