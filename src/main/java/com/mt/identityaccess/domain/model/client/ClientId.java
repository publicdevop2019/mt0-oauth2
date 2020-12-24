package com.mt.identityaccess.domain.model.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ClientId implements Serializable {
    @Column(unique = true, updatable = false)
    private String clientId;
}
