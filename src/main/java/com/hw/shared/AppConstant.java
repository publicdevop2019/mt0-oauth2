package com.hw.shared;

public class AppConstant {
    public static final String HTTP_HEADER_ERROR_ID = "errorId";
    public static final String HTTP_HEADER_SUPPRESS = "suppressEx";
    public static final String HTTP_HEADER_SUPPRESS_REASON_CHANGE_ID_EXIST = "changeIdExist";
    public static final String HTTP_PARAM_QUERY = "query";
    public static final String HTTP_PARAM_PAGE = "page";
    public static final String HTTP_PARAM_SKIP_COUNT = "config";
    public static final String HTTP_HEADER_CHANGE_ID = "changeId";
    public static final String HTTP_HEADER_AUTHORIZATION = "authorization";
    public static final String PATCH_OP_TYPE_REMOVE = "remove";
    public static final String PATCH_OP_TYPE_SUM = "sum";
    public static final String PATCH_OP_TYPE_ADD = "add";
    public static final String PATCH_OP_TYPE_DIFF = "diff";
    public static final String PATCH_OP_TYPE_REPLACE = "replace";
    public static final String COMMON_ENTITY_ID = "id";
    public static final String CHANGE_REVOKED = "_REVOKED";
    public static final String EXCHANGE_ROLLBACK = "rollback";
    public static final String CACHE_QUERY_PREFIX = "-query";
    public static final String CACHE_ID_PREFIX = "-id";
    private AppConstant() {
    }
}
