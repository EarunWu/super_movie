package com.example.super_movie.annotations;

import java.lang.annotation.*;

/**
 * JWT请求忽略注解
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JwtIgnore {
}
