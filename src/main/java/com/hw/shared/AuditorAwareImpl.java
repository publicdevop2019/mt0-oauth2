package com.hw.shared;

import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Optional<HttpServletRequest> httpServletRequest = Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                .filter(requestAttributes -> ServletRequestAttributes.class.isAssignableFrom(requestAttributes.getClass()))
                .map(requestAttributes -> ((ServletRequestAttributes) requestAttributes))
                .map(ServletRequestAttributes::getRequest);
        if (httpServletRequest.isEmpty())
            return Optional.of("NOT_HTTP");
        String authorization = httpServletRequest.get().getHeader("authorization");
        return Optional.ofNullable(authorization == null ? "EMPTY_AUTH_HEADER" : ServiceUtility.getUserId(authorization) == null ? ServiceUtility.getClientId(authorization) : ServiceUtility.getUserId(authorization));
    }
}
