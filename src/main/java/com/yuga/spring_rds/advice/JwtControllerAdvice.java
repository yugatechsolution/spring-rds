package com.yuga.spring_rds.advice;

import com.yuga.spring_rds.controller.ContactController;
import com.yuga.spring_rds.repository.UserRepository;
import com.yuga.spring_rds.util.Constants;
import com.yuga.spring_rds.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@ControllerAdvice(assignableTypes = {ContactController.class})
public class JwtControllerAdvice {

  @Autowired private UserRepository userRepository;

  /** This method runs before every controller method and logs the request details. */
  @InitBinder
  public void beforeMethodExecution(HttpServletRequest request) {
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

  @ModelAttribute
  public void afterMethodExecution() {
    RequestContext.clear();
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<String> handleJwtException(JwtException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body("Invalid or expired JWT token: " + ex.getMessage());
  }
}
