package com.example.demo.security;

import com.auth0.jwt.JWT;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JWTTokenFactory {

    public static String createFor(String username) {

        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(HMAC512(SecurityConstants.SECRET.getBytes()));
    }
}
