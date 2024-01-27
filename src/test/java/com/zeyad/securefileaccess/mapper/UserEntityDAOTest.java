package com.zeyad.securefileaccess.mapper;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zeyad.securefileaccess.dao.UserEntityDAO;
import com.zeyad.securefileaccess.entity.UserEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class UserEntityDAOTest {

    @Mock
    private EntityManager entityManagerMock;

    @Mock
    private JPAQueryFactory queryFactoryMock;

//    @Mock
//    private QUserEntity userEntityMock;

    @Mock
    private UserEntity userMock;

    private UserEntityDAO userEntityDAO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userEntityDAO = new UserEntityDAO(entityManagerMock);
        ReflectionTestUtils.setField(userEntityDAO, "queryFactory", queryFactoryMock);
    }

    @Test
    public void testGetUserById_whenUserIsExisted_shouldReturnUserEntity() {
        String userId = "testUserId";


    }

    @Test
    public void testGetUserByUsername() {
//        String username = "testUsername";
//
//        when(queryFactoryMock.select(userEntityMock)).thenReturn(queryFactoryMock);
//        when(queryFactoryMock.from(userEntityMock)).thenReturn(queryFactoryMock);
//        when(userEntityMock.username).thenReturn(userEntityMock);
//        when(userEntityMock.eq(username)).thenReturn(userEntityMock);
//        when(queryFactoryMock.where(userEntityMock)).thenReturn(queryFactoryMock);
//        when(queryFactoryMock.fetchOne()).thenReturn(userMock);
//
//        UserEntity resultUser = userEntityDAO.getUserByUsername(username);
//
//        assertEquals(userMock, resultUser);
    }
}

