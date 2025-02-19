package com.yuga.spring_rds.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordUtil {
  private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  /**
   * Hash a password using BCrypt.
   *
   * @param rawPassword The plain text password
   * @return The hashed password
   */
  public static String hashPassword(String rawPassword) {
    return passwordEncoder.encode(rawPassword);
  }

  /**
   * Check if a raw password matches a hashed password.
   *
   * @param rawPassword The plain text password
   * @param hashedPassword The stored hashed password
   * @return true if passwords match, false otherwise
   */
  public static boolean matches(String rawPassword, String hashedPassword) {
    return passwordEncoder.matches(rawPassword, hashedPassword);
  }
}
