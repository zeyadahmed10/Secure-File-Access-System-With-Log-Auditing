package com.zeyad.securefileaccess.dao;

import com.zeyad.securefileaccess.entity.AuditLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogsEntityRepository extends JpaRepository<AuditLogsEntity, Integer> {
}