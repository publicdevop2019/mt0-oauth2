package com.mt.identityaccess.application.revoke_token;

public class RevokeTokenPaging {
    private final long pageNumber;
    private final int pageSize;
    private String value;

    public String value() {
        return value;
    }

    public RevokeTokenPaging(String pagingParamStr) {
        pageNumber = 0;
        pageSize = 0;
        value = pagingParamStr;
    }

    public RevokeTokenPaging() {
        pageNumber = 0L;
        pageSize = 10;
    }

    public RevokeTokenPaging(Long pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public RevokeTokenPaging nextPage() {
        return new RevokeTokenPaging(pageNumber + 1, pageSize);
    }
}
