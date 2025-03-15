package com.yuga.spring_rds.service;

import com.yuga.spring_rds.domain.User;
import com.yuga.spring_rds.dto.UserDTO;
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

  public ResponseEntity<?> registerUser(User user) {
    if (userRepository.findByUsernameOrEmail(user.getUsername(), user.getEmail()).isPresent()) {
      return ResponseEntity.badRequest().body("User already registered");
    }
    String hashedPassword = PasswordUtil.hashPassword(user.getPassword());
    user.setPassword(hashedPassword);
    userRepository.save(user);
    return ResponseEntity.ok()
        .body(
            UserDTO.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(JwtUtil.generateToken(user.getUsername()))
                .build());
  }

  public ResponseEntity<?> authenticateUser(String identifier, String password) {
    Optional<User> userOpt = userRepository.findByUsernameOrEmail(identifier, identifier);

    if (userOpt.isPresent()) {
      User user = userOpt.get();
      if (PasswordUtil.matches(password, user.getPassword())) {
        return ResponseEntity.ok()
            .body(
                UserDTO.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .token(JwtUtil.generateToken(user.getUsername()))
                    .build());
      }
    }
    return ResponseEntity.status(403).body("User does not exist!");
  }
}
