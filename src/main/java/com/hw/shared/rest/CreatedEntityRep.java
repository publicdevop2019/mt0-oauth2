package com.hw.shared.rest;

import lombok.Data;

@Data
public class CreatedEntityRep extends CreatedRep {
    private Long id;

    public CreatedEntityRep(IdBasedEntity entity) {
        this.id = entity.getId();
    }

    public CreatedEntityRep() {

    }
}
