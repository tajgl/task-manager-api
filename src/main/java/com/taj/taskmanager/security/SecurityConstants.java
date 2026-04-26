package com.taj.taskmanager.security;

import org.springframework.beans.factory.annotation.Value;

public class SecurityConstants {

    public static String JWT_SECRET;
    public static long JWT_EXPIRATION;

    @Value("${jwt.secret}")
    public void setJwtSecret(String secret) {
        JWT_SECRET = secret;
    }

    @Value("${jwt.expiration}")
    public void setJwtExpiration(long expiration) {
        JWT_EXPIRATION = expiration;
    }
}
