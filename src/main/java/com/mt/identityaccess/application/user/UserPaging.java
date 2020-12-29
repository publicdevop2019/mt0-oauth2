package com.mt.identityaccess.application.user;

public class UserPaging {
    private final long pageNumber;
    private final int pageSize;
    private String value;

    public String value() {
        return value;
    }

    public UserPaging(String pagingParamStr) {
        pageNumber = 0;
        pageSize = 0;
        value = pagingParamStr;
    }

    public UserPaging() {
        pageNumber = 0L;
        pageSize = 10;
    }

    public UserPaging(Long pageNumber, Integer pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public UserPaging nextPage() {
        return new UserPaging(pageNumber + 1, pageSize);
    }
}
