package com.mt.access.domain.model.endpoint;

import com.mt.common.domain.model.sql.converter.StringSetConverter;
import lombok.Getter;

import javax.persistence.Convert;
import javax.persistence.Embeddable;
import java.util.Set;

@Getter
@Embeddable
public class CorsConfig {
    @Convert(converter = StringSetConverter.class)
    private Set<String> origin;
    private boolean credentials;
    @Convert(converter = StringSetConverter.class)
    private Set<String> allowedHeaders;
    @Convert(converter = StringSetConverter.class)
    private Set<String> exposedHeaders;
    private Long maxAge;


    public CorsConfig() {
    }
}