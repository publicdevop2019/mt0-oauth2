package com.mt.identityaccess.domain.model.client;

public class ClientPaging implements Cloneable {
    private final long pageNumber;
    private final int pageSize;
    public String value;

    public ClientPaging(String pagingParamStr) {
        pageNumber = 0;
        pageSize = 0;
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
