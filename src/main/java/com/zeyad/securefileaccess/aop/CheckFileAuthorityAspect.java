package com.zeyad.securefileaccess.aop;

import com.zeyad.securefileaccess.dao.FileDAO;
import com.zeyad.securefileaccess.exceptions.ForbiddenFileAccessException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckFileAuthorityAspect {
    @Autowired
    private FileDAO fileDAO;
    @Before(value = "@annotation(com.zeyad.securefileaccess.annotation.CheckFileAuthority)")
    public void checkFileAuthority(JoinPoint joinPoint){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal==null)
            return;
        Jwt token = (Jwt) principal;
        String userId = token.getClaimAsString("sub");
        Object[] args = joinPoint.getArgs();
        Integer fileId = null;
        for(var arg: args){
            if (arg instanceof Integer){
                fileId = (Integer) arg;
                break;
            }
        }
        if(!fileDAO.checkAuthority(fileId, userId))
            throw new ForbiddenFileAccessException("Not authorized to access file with id: " + fileId);
    }

}
