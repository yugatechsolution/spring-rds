package com.yuga.spring_rds.advice;

import io.jsonwebtoken.JwtException;
import java.util.Arrays;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<String> handleJwtException(JwtException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body("Invalid or expired JWT token: " + ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericException(Exception ex) {
    log.error(
        "Generic Exception Caught: {}",
        Arrays.stream(ex.getStackTrace())
            .limit(10)
            .map(StackTraceElement::toString)
            .collect(Collectors.joining("\n")));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body("Generic Exception: " + ex.getMessage());
  }
}
