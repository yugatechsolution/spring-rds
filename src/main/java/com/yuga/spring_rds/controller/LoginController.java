package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.model.User;
import com.yuga.spring_rds.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class LoginController {

  @Autowired private UserService userService;

  @PostMapping("/login")
  public User login(@RequestHeader String username, @RequestHeader String password) {
    return User.builder().username(username).email("abc@gmail.com").build();
  }
}
