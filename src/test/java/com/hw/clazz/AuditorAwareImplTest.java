package com.hw.clazz;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AuditorAwareImplTest {

    AuditorAwareImpl auditorAware = new AuditorAwareImpl();

    @Test
    public void getCurrentAuditor() {
        String mockedName = UUID.randomUUID().toString();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn(mockedName);
        SecurityContextHolder.setContext(securityContext);
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        Assert.assertEquals(false, currentAuditor.isEmpty());
        Assert.assertEquals(mockedName, currentAuditor.get());
    }

    @Test(expected = NoSuchElementException.class)
    public void getCurrentAuditor_noAuth() {
        String mockedName = UUID.randomUUID().toString();
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        Assert.assertEquals(true, currentAuditor.isEmpty());
        currentAuditor.get();
    }
}