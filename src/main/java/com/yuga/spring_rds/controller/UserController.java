package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.model.User;
import com.yuga.spring_rds.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

  @Autowired private UserService userService;

  // ✅ Register User
  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody User user) {
    String response = userService.registerUser(user);
    if (response.equals("User registered successfully!")) {
      return ResponseEntity.ok(response);
    } else {
      return ResponseEntity.badRequest().body(response);
    }
  }

  // ✅ Login with either username OR email
  @PostMapping("/login")
  public ResponseEntity<String> loginUser(@RequestParam String id, @RequestParam String password) {
    if (userService.loginUser(id, password)) {
      return ResponseEntity.ok("Login successful!");
    }
    return ResponseEntity.badRequest().body("Invalid username/email or password");
  }
}
