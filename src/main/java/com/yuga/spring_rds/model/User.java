package com.yuga.spring_rds.model;

import com.yuga.spring_rds.util.PasswordUtil;
import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
  private Long id;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false, unique = true, length = 100)
  private String email;

  @Column(nullable = false, length = 64)
  private String password;

  // ✅ Hash password before storing
  public void setPassword(String plainPassword) {
    this.password = PasswordUtil.hashPassword(plainPassword);
  }
}
