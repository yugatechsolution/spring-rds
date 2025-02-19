package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.model.User;
import com.yuga.spring_rds.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  @Autowired private UserService userService;

  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@RequestBody User user) {
    return userService.registerUser(user);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestParam String id, @RequestParam String password) {
    return userService.authenticateUser(id, password);
  }
}
