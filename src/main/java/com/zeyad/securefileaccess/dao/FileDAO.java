package com.zeyad.securefileaccess.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.entity.FileEntity;
import com.zeyad.securefileaccess.entity.QFileEntity;
import com.zeyad.securefileaccess.entity.QUserEntity;
import com.zeyad.securefileaccess.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class FileDAO {

    private final JPAQueryFactory queryFactory;

    public FileDAO(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    public List<FileEntity> getAllFilesForUser(String userId){
        QUserEntity user = QUserEntity.userEntity;
        QFileEntity file = QFileEntity.fileEntity;
        return queryFactory.select(file).from(file)
                .join(file.authorizedUsers, user).on(user.id.eq(userId)).fetch();
    }
    public FileEntity getFileById(String userId, Integer fileId){
        QUserEntity user = QUserEntity.userEntity;
        QFileEntity file = QFileEntity.fileEntity;
        return queryFactory.select(file).from(file).where(file.id.eq(fileId)).fetchOne();
    }
    public List<FileEntity> getAllFiles(){
        QFileEntity file = QFileEntity.fileEntity;
        return queryFactory.select(file).from(file).fetch();
    }
    @Transactional
    public long updateFileEntity(Integer fileId, String newName, String newContent, String newChecksum) {
        QFileEntity file = QFileEntity.fileEntity;
        return queryFactory
                .update(file)
                .set(file.name, newName)
                .set(file.content, newContent)
                .set(file.checksum, newChecksum)
                .set(file.lastUpdated, Timestamp.from(Instant.now()))
                .where(file.id.eq(fileId))
                .execute();
    }
    @Transactional
    public long deleteFileEntity(Integer fileId){
        QFileEntity file = QFileEntity.fileEntity;
        return queryFactory.delete(file).where(file.id.eq(fileId)).execute();
    }

    public boolean checkAuthority(Integer fileId, String userId) {
        QUserEntity user = QUserEntity.userEntity;
        QFileEntity file = QFileEntity.fileEntity;
        var fileEntity = queryFactory.select(file).from(file).where(file.id.eq(fileId)).fetchOne();
        if(fileEntity==null)
            throw new ResourceNotFoundException("No file with id " + fileId);
        var result = queryFactory.select(file).from(file).join(file.authorizedUsers, user)
                .on(file.id.eq(fileId)).on(user.id.eq(userId)).fetchOne();
        return result != null;
    }
//    @Transactional
//    public long updateFileContent()


}
