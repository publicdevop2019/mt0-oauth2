package com.mt.identityaccess.application.client;

public class EndpointQuery {
    private String value;

    public String value() {
        return value;
    }

    public EndpointQuery(String queryParam) {
        this.value = queryParam;
    }
}
