package com.yuga.spring_rds.advice;

import com.yuga.spring_rds.repository.UserRepository;
import com.yuga.spring_rds.util.Constants;
import com.yuga.spring_rds.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class JwtAspect {

  @Autowired private UserRepository userRepository;

  /**
   * This aspect will execute before any method annotated with @JwtSecured or inside a class
   * annotated with @JwtSecured.
   */
  @Around(
      "@annotation(com.yuga.spring_rds.util.JwtSecured) || @within(com.yuga.spring_rds.util.JwtSecured)")
  public Object validateJwt(ProceedingJoinPoint joinPoint) throws Throwable {
    ServletRequestAttributes attributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

    if (attributes == null) {
      throw new IllegalStateException("No request context found");
    }

    HttpServletRequest request = attributes.getRequest();
    log.info("🚀 Incoming secured request: {} {}", request.getMethod(), request.getRequestURI());

    String username =
        JwtUtil.validateTokenAndRetrieveUsername(request.getHeader(Constants.AUTHORIZATION_HEADER));

    userRepository
        .findByUsernameOrEmail(username, username)
        .ifPresentOrElse(
            RequestContext::setUser,
            () -> {
              throw new RuntimeException("No user found with username in JWT");
            });

    try {
      return joinPoint.proceed();
    } finally {
      RequestContext.clear();
      log.info("✅ JWT validation completed for: {}", joinPoint.getSignature().toShortString());
    }
  }
}
