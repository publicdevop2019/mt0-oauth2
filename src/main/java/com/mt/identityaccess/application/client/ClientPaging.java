package com.mt.identityaccess.application.client;

public class ClientPaging{
    private final long pageNumber;
    private final int pageSize;
    public String value;

    public ClientPaging(String pagingParamStr) {
        pageNumber = 0;
        pageSize = 0;
        value = pagingParamStr;
    }

    public ClientPaging() {
        pageNumber = 0L;
        pageSize = 10;
    }

    public ClientPaging(Long pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public ClientPaging nextPage() {
        return new ClientPaging(pageNumber + 1, pageSize);
    }
}
