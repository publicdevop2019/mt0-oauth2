package com.mt.identityaccess.application.client;

public class QueryConfig {
    private String value;

    public String value() {
        return value;
    }

    public QueryConfig(String configParam) {
        value = configParam;
    }
}