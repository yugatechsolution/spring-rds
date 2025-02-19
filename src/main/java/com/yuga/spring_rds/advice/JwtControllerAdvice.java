package com.yuga.spring_rds.advice;

import com.yuga.spring_rds.controller.ContactController;
import com.yuga.spring_rds.util.Constants;
import com.yuga.spring_rds.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

@Slf4j
@ControllerAdvice(assignableTypes = {ContactController.class})
public class JwtControllerAdvice {

  /** This method runs before every controller method and logs the request details. */
  @ModelAttribute
  public void beforeMethodExecution(HttpServletRequest request) {
    log.info("🚀 Incoming request: {} {}", request.getMethod(), request.getRequestURI());
    JwtUtil.validateToken(request.getHeader(Constants.AUTHORIZATION_HEADER));
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<String> handleJwtException(JwtException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body("Invalid or expired JWT token: " + ex.getMessage());
  }
}
