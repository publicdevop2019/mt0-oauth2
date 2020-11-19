package com.hw.shared.idempotent;

public enum OperationType {
    POST,
    PATCH_BATCH,
    PATCH_BY_ID,
    PUT,
    RESTORE_LAST_VERSION,
    EMPTY_OPT,
    RESTORE_DELETE,
    CANCEL_CREATE,
    DELETE_BY_QUERY
}
