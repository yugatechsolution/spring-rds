package com.yuga.spring_rds.service;

import com.yuga.spring_rds.model.User;
import com.yuga.spring_rds.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

  @Autowired private UserRepository userRepository;

  public User createUser(User user) {
    log.info("Creating the user...");
    return userRepository.save(user);
  }

  public User getUserByName(String name) {
    log.info("Get the user details by name: {}", name);
    return userRepository.findByName(name);
  }

  public List<User> getAllUsers() {
    log.info("Getting all the users...");
    return userRepository.findAll();
  }
}
