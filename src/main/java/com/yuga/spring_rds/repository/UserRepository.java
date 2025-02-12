package com.yuga.spring_rds.repository;

import com.yuga.spring_rds.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  User findByName(String name);
}
