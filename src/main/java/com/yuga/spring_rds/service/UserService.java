package com.yuga.spring_rds.service;

import com.yuga.spring_rds.model.User;
import com.yuga.spring_rds.repository.UserRepository;
import com.yuga.spring_rds.util.JwtUtil;
import com.yuga.spring_rds.util.PasswordUtil;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired private UserRepository userRepository;

  public ResponseEntity<String> registerUser(User user) {
    if (userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail()).isPresent()) {
      return ResponseEntity.badRequest().body("User already registered");
    }
    String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
    user.setPassword(hashedPassword);
    userRepository.save(user);
    return ResponseEntity.ok().body("User registered successfully!");
  }

  public ResponseEntity<String> authenticateUser(String identifier, String password) {
    Optional<User> userOpt = userRepository.findByUsernameOrEmail(identifier, identifier);

    if (userOpt.isPresent()) {
      User user = userOpt.get();
      if (PasswordUtil.matches(password, user.getPassword())) {
        return ResponseEntity.ok().body(JwtUtil.generateToken(user.getId()));
      }
    }
    return ResponseEntity.status(403).body("User does not exist!");
  }
}
