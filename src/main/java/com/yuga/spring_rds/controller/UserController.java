package com.yuga.spring_rds.controller;

import com.yuga.spring_rds.model.User;
import com.yuga.spring_rds.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  @Autowired private UserService userService;

  @PostMapping
  public User createUser(@RequestBody User user) {
    return userService.createUser(user);
  }

  @GetMapping
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{name}")
  public User getUserByName(@PathVariable String name) {
    return userService.getUserByName(name);
  }
}
