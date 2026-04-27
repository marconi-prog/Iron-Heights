package com.prison.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TesteSenha {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("123456");
        System.out.println(hash);
    }
}