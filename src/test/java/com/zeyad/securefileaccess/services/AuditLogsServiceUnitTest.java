package com.zeyad.securefileaccess.services;

import com.zeyad.securefileaccess.dao.AuditLogsEntityRepository;
import com.zeyad.securefileaccess.entity.AuditLogsEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class AuditLogsServiceUnitTest {

    @Mock
    AuditLogsEntityRepository auditLogsEntityRepository;
    @InjectMocks
    AuditLogsService auditLogsService;
    @Test
    void saveAuditLog_whenAuditLogsEntityPassedAsParameter_shouldSaveAuditLogsEntity() {
        //arrange
        AuditLogsEntity auditLogs = AuditLogsEntity.builder()
                .username("username").action("GET").request("api/v1/files")
                .fileChecksum("checksum").timestamp(Timestamp.from(Instant.now())).build();
        //act
        doReturn(auditLogs).when(auditLogsEntityRepository).save(auditLogs);
        auditLogsService.saveAuditLog(auditLogs);
        //assert
        verify(auditLogsEntityRepository, times(1)).save(auditLogs);
    }
}