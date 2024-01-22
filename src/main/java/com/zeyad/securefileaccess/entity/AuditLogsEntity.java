package com.zeyad.securefileaccess.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "audit_logs", schema = "public")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;
    private String username;
    private String action;
    private Timestamp timestamp;
    private String request;
    private String fileChecksum;
}
