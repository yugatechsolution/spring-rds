package com.yuga.spring_rds.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtUtil {

  private static final String SECRET_KEY = "your-very-secure-random-secret-key"; // Change this!
  private static final long EXPIRATION_TIME = 86400000; // 1 day in milliseconds

  private static final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

  public static String generateToken(Long id) {
    return Jwts.builder()
        .setSubject(String.valueOf(id))
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public static String validateTokenAndRetrieveUserId(String token) {
    if (Objects.isNull(token)) throw new JwtException("Bearer Token not present!");
    if (token.startsWith("Bearer ")) token = token.substring(7);
    try {
      return Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody()
          .getSubject();
    } catch (JwtException e) {
      log.error("Token validation failed: ", e);
      throw e;
    }
  }
}
