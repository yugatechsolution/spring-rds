package com.yuga.spring_rds.advice;

import com.yuga.spring_rds.model.User;
import org.springframework.stereotype.Component;

@Component
public class RequestContext {

  private static final ThreadLocal<User> userIdHolder = new ThreadLocal<>();

  public static void setUser(User user) {
    userIdHolder.set(user);
  }

  public static Long getUserId() {
    return userIdHolder.get().getId();
  }

  public static User getUser() {
    return userIdHolder.get();
  }

  public static void clear() {
    userIdHolder.remove();
  }
}
