package com.hw.shared.idempotent;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CreateDeleteCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String query;
    private OperationType operationType;

}
