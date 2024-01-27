package com.zeyad.securefileaccess.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.entity.FileEntity;
import com.zeyad.securefileaccess.entity.QFileEntity;
import com.zeyad.securefileaccess.entity.QUserEntity;
import com.zeyad.securefileaccess.exceptions.ResourceNotFoundException;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
public class FileDAO {

    private final JPAQueryFactory queryFactory;
    @Autowired
    private FileEntityRepository fileEntityRepository;
    private EntityManager entityManager;
    public FileDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.queryFactory = new JPAQueryFactory(this.entityManager);
    }
    public List<FileEntity> getAllFilesForUser(String userId, String name, int page, int size){
        QUserEntity user = QUserEntity.userEntity;
        QFileEntity file = QFileEntity.fileEntity;
        return queryFactory.select(file).from(file)
                .join(file.authorizedUsers, user).on(user.id.eq(userId))
                .where(file.name.like("%"+name+"%"))
                .orderBy(file.id.asc()).limit(size).offset((long) page *size).fetch();
    }
    public FileEntity getFileById(Integer fileId){
        QUserEntity user = QUserEntity.userEntity;
        QFileEntity file = QFileEntity.fileEntity;
        return queryFactory.select(file).from(file).where(file.id.eq(fileId)).fetchOne();
    }
    public List<FileEntity> getAllFiles(String name, int page, int size){
        QFileEntity file = QFileEntity.fileEntity;
        return queryFactory.select(file).from(file).where(file.name.like("%"+name+"%")).orderBy(file.id.asc())
                .limit(size).offset((long) page *size).fetch();
    }
    public FileEntity updateFileEntity(Integer fileId, String newName, String newContent, String newChecksum) {
        var file =  fileEntityRepository.findById(fileId).orElseThrow(
                ()->new ResourceNotFoundException("No file with id: "+fileId)
        );
        file.setName(newName);
        file.setContent(newContent);
        file.setChecksum(newChecksum);
        file.setLastUpdated(Timestamp.from(Instant.now()));
        return fileEntityRepository.save(file);
    }
    public FileEntity addFile(FileEntity file){
        return fileEntityRepository.save(file);
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
            throw new ResourceNotFoundException("No file with id: " + fileId);
        var result = queryFactory.select(file).from(file).join(file.authorizedUsers, user)
                .on(file.id.eq(fileId)).on(user.id.eq(userId)).fetchOne();
        return result != null;
    }

}
