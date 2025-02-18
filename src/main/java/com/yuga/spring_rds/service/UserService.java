package com.yuga.spring_rds.service;

import com.yuga.spring_rds.model.User;
import com.yuga.spring_rds.repository.UserRepository;
import com.yuga.spring_rds.util.PasswordUtil;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired private UserRepository userRepository;

  // ✅ Register User
  public String registerUser(User user) {
    if (userRepository.findByUsername(user.getUsername()).isPresent()
        || userRepository.findByEmail(user.getEmail()).isPresent()) {
      return "Username or Email already in use";
    }

    user.setPassword(PasswordUtil.hashPassword(user.getPassword()));
    userRepository.save(user);
    return "User registered successfully!";
  }

  // ✅ Login with either username OR email
  public boolean loginUser(String identifier, String password) {
    Optional<User> userOptional =
        userRepository.findByUsername(identifier).or(() -> userRepository.findByEmail(identifier));

    if (userOptional.isPresent()) {
      User user = userOptional.get();
      String hashedPassword = PasswordUtil.hashPassword(password);
      return hashedPassword.equals(user.getPassword());
    }
    return false;
  }
}
