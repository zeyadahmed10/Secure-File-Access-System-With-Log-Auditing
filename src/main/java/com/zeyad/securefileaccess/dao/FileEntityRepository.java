package com.zeyad.securefileaccess.dao;

import com.zeyad.securefileaccess.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileEntityRepository extends JpaRepository<FileEntity, Integer> {
}