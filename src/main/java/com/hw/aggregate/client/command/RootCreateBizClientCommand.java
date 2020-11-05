package com.hw.aggregate.client.command;

import com.hw.aggregate.client.model.BizClientAuthorityEnum;
import com.hw.aggregate.client.model.GrantTypeEnum;
import com.hw.aggregate.client.model.ScopeEnum;
import com.hw.config.SelfSignedTokenConfig;
import com.hw.shared.EurekaRegistryHelper;
import com.hw.shared.validation.ValidationErrorException;
import com.hw.shared.validation.ValidationFailedException;
import com.hw.shared.validation.ValidatorMessage;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Data
@Slf4j
public class RootCreateBizClientCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String clientSecret;
    private boolean hasSecret;
    private String description;
    private String name;

    private Set<GrantTypeEnum> grantTypeEnums;

    private Set<BizClientAuthorityEnum> grantedAuthorities;

    private Set<ScopeEnum> scopeEnums;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Set<String> resourceIds;

    private Boolean resourceIndicator;

    private Boolean autoApprove;
}
