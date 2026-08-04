package com.bank.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.bank.dto.AuthResponse;
import com.bank.dto.LoginRequest;
import com.bank.dto.RegisterRequest;
import com.bank.entity.Role;
import com.bank.entity.User;
import com.bank.enums.RoleType;
import com.bank.exception.EmailAlreadyExistsException;
import com.bank.exception.PhoneAlreadyExistsException;
import com.bank.repository.RoleRepository;
import com.bank.repository.UserRepository;
import com.bank.security.CustomUserDetails;
import com.bank.security.JwtUtil;
import com.bank.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        logger.info("Registration request received for email: {}",
                request.getEmail());

        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {

            logger.warn("Registration failed. Email already exists: {}",
                    request.getEmail());

            throw new EmailAlreadyExistsException(request.getEmail());
        }

        // Check duplicate phone
        if (userRepository.existsByPhone(request.getPhone())) {

            logger.warn("Registration failed. Phone already exists: {}",
                    request.getPhone());

            throw new PhoneAlreadyExistsException(request.getPhone());
        }

        // Get CUSTOMER role
        Role customerRole = roleRepository.findByName(RoleType.CUSTOMER)
                .orElseThrow(() ->
                        new RuntimeException("Default CUSTOMER role not found"));

        // Create user
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(customerRole);
        user.setEnabled(true);

        userRepository.save(user);

        logger.info("User registered successfully: {}", user.getEmail());

        return new AuthResponse(null, "Registration Successful");
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        logger.info("Login attempt for email: {}",
                request.getEmail());

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail(),
                                request.getPassword()));

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        String token =
                jwtUtil.generateToken(userDetails.getUsername());

        logger.info("User logged in successfully: {}",
                userDetails.getUsername());

        return new AuthResponse(token, "Login Successful");
    }
}