package com.mt.identityaccess.application.endpoint;

public class EndpointPaging {
    private final long pageNumber;
    private final int pageSize;
    private String value;

    public String value() {
        return value;
    }

    public EndpointPaging(String pagingParamStr) {
        pageNumber = 0;
        pageSize = 0;
        value = pagingParamStr;
    }

    public EndpointPaging() {
        pageNumber = 0L;
        pageSize = 10;
    }

    public EndpointPaging(Long pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public EndpointPaging nextPage() {
        return new EndpointPaging(pageNumber + 1, pageSize);
    }
}
