package com.yuga.spring_rds.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE}) // Can be applied to methods and classes
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
public @interface JwtSecured {}
