package com.bank.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "admin@123";

        System.out.println("Password : " + password);
        System.out.println("BCrypt   : " + encoder.encode(password));
    }
}