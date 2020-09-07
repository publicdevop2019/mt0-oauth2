package com.hw.shared.idempotent;

public enum OperationType {
    POST,
    PATCH_BATCH,
    PATCH_BY_ID,
    PUT,
    RESTORE_DELETE,
    CANCEL_CREATE,
    DELETE_BY_ID,
    DELETE_BY_QUERY;
}
