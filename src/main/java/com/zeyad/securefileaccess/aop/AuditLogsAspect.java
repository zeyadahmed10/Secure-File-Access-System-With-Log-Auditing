package com.zeyad.securefileaccess.aop;

import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.entity.AuditLogsEntity;
import com.zeyad.securefileaccess.services.AuditLogsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.sql.Timestamp;
import java.time.Instant;


@Aspect
@Component
@Slf4j
public class AuditLogsAspect {
    @Autowired
    private AuditLogsService auditLogsService;
    @AfterReturning(value = "@annotation(com.zeyad.securefileaccess.annotation.AuditLogs)", returning = "result")
    public void auditLog(Object result) throws Throwable {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal==null)
                return;
        Jwt token = (Jwt) principal;
        String username = token.getClaimAsString("preferred_username");
        HttpServletRequest request = getRequest();
        String httpMethod = request.getMethod();
        String endpoint = request.getRequestURI();
        String checksum = getChecksum(result, httpMethod, endpoint);
        log.info("User '{}' performed a {} request to '{}'. Checksum: {}", username, httpMethod, endpoint, checksum);
        AuditLogsEntity auditLogs = AuditLogsEntity.builder()
                .username(username).action(httpMethod).request(endpoint)
                .fileChecksum(checksum).timestamp(Timestamp.from(Instant.now())).build();
        auditLogsService.saveAuditLog(auditLogs);
    }
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes.getRequest();
    }

    private String getChecksum(Object result, String method, String endpoint){
        if(method.equals("DELETE"))
            return "";
        if(method.equals("GET") && endpoint.equals("/api/v1/files"))
            return "";
        return ((FileResponseDTO) result).getChecksum();
    }
}
