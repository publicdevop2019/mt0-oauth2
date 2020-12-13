package com.mt.identityaccess.domain.model.client;

public class QueryPagingParam implements Cloneable {
    private final long pageNumber;
    private final int pageSize;

    public QueryPagingParam(String pagingParamStr) {
        pageNumber = 0;
        pageSize = 0;
    }

    public QueryPagingParam() {
        pageNumber = 0L;
        pageSize = 10;
    }

    public QueryPagingParam(Long pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public QueryPagingParam nextPage() {
        return new QueryPagingParam(pageNumber + 1, pageSize);
    }
}
