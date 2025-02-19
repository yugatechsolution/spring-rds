package com.yuga.spring_rds.advice;

import com.yuga.spring_rds.repository.UserRepository;
import com.yuga.spring_rds.util.Constants;
import com.yuga.spring_rds.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class JwtAspect {

  @Autowired private UserRepository userRepository;

  @Before("execution(* com.yuga.spring_rds.controller.ContactController.*(..))")
  public void beforeMethodExecution(JoinPoint joinPoint) {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attributes == null) {
      throw new IllegalStateException("No request attributes found");
    }

    HttpServletRequest request = attributes.getRequest();
    log.info("🚀 Incoming request: {} {}", request.getMethod(), request.getRequestURI());

    String userId =
        JwtUtil.validateTokenAndRetrieveUserId(request.getHeader(Constants.AUTHORIZATION_HEADER));

    userRepository
        .findById(Long.valueOf(userId))
        .ifPresentOrElse(
            RequestContext::setUser,
            () -> {
              throw new RuntimeException("No user found with user id in JWT");
            });
  }

  @After("execution(* com.yuga.spring_rds.controller.ContactController.*(..))")
  public void afterMethodExecution() {
    RequestContext.clear();
  }
}
