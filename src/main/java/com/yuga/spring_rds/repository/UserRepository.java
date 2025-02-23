package com.yuga.spring_rds.repository;

import com.yuga.spring_rds.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsernameOrEmail(String username, String email);
}
