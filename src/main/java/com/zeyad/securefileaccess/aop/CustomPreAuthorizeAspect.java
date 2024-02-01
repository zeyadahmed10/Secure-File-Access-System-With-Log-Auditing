package com.zeyad.securefileaccess.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zeyad.securefileaccess.annotation.CustomPreAuthorize;
import com.zeyad.securefileaccess.exceptions.NotAuthorizedException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

@Aspect
@Component
public class CustomPreAuthorizeAspect {
    @Before(value = "@annotation(com.zeyad.securefileaccess.annotation.CheckFileAuthority)")
    public void checkRoleAuthority(JoinPoint joinPoint) throws JsonProcessingException {
        Object principal= SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(principal ==null)
            return;
        Jwt jwt = (Jwt)principal;
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JsonNode jwtJson = objectMapper.readTree(objectMapper.writeValueAsString(jwt));
        JsonNode roles = jwtJson.get("realm_access").get("roles");
        String annotationRole = getAnnotationValue(joinPoint);
        for(var item: roles){
            if(item.asText().equals(annotationRole))
                return;
        }
        throw new NotAuthorizedException();

    }
    public static String getAnnotationValue(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CustomPreAuthorize methodAnnotation = method.getAnnotation(CustomPreAuthorize.class);
        if (methodAnnotation != null) {
            return methodAnnotation.role();
        } else {
            Class<?> targetClass = joinPoint.getTarget().getClass();
            CustomPreAuthorize classAnnotation = targetClass.getAnnotation(CustomPreAuthorize.class);
            if (classAnnotation != null) {
                return classAnnotation.role();
            }
        }
        return null; // Or throw an exception if annotation is not found
    }
}
