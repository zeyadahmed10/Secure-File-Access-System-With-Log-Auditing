package com.zeyad.securefileaccess.aop;

import com.zeyad.securefileaccess.services.AuditLogsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditLogsAspectUnitTest {
    @InjectMocks
    private AuditLogsAspect auditLogsAspect;
    @Mock
    private AuditLogsService auditLogsService;
    @BeforeEach
    void setUp() {
    }

    //@Test
    void testAuditLog_whenHttpMethodIsUpdate_shouldAuditLogWithoutChecksum() {
        String username = "username";
        Jwt token = mock(Jwt.class);
        when(token.getClaimAsString("preferred_username")).thenReturn(username);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        ServletRequestAttributes mockRequestAttributes = mock(ServletRequestAttributes.class);
        RequestContextHolder.setRequestAttributes(mockRequestAttributes);
        
    }
}