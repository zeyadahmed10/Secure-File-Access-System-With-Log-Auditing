package com.zeyad.securefileaccess.dao;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zeyad.securefileaccess.entity.QUserEntity;
import com.zeyad.securefileaccess.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class UserEntityDAO {
    private final JPAQueryFactory queryFactory;

    public UserEntityDAO(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    public UserEntity getUserById(String userId){
        QUserEntity user = QUserEntity.userEntity;
        return queryFactory.select(user).from(user).where(user.id.eq(userId)).fetchOne();
    }
    public UserEntity getUserByUsername(String username){
        QUserEntity user = QUserEntity.userEntity;
        return queryFactory.select(user).from(user).where(user.username.eq(username)).fetchOne();
    }
}
