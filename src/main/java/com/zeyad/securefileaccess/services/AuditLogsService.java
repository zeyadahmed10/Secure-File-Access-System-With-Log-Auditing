package com.zeyad.securefileaccess.services;

import com.zeyad.securefileaccess.entity.AuditLogsEntity;
import com.zeyad.securefileaccess.dao.AuditLogsEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogsService {

    @Autowired
    private AuditLogsEntityRepository auditLogsEntityRepository;
    public void saveAuditLog(AuditLogsEntity auditLogsEntity){
        auditLogsEntityRepository.save(auditLogsEntity);
    }
}
