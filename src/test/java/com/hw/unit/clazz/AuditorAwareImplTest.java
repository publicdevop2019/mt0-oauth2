package com.hw.unit.clazz;

import com.hw.shared.AuditorAwareImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
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
    public void getCurrentAuditor_noAuth() {
        Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
        Assert.assertEquals(false, currentAuditor.isEmpty());
        Assert.assertEquals("HttpServletRequest_Empty", currentAuditor.get());
    }
}