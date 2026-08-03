package com.bank.service.impl;

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

        // Check if email already exists
    	if (userRepository.existsByEmail(request.getEmail())) {
    	    throw new EmailAlreadyExistsException(request.getEmail());
    	}

        // Check if phone already exists
    	if (userRepository.existsByPhone(request.getPhone())) {
    	    throw new PhoneAlreadyExistsException(request.getPhone());
    	}

        // Get CUSTOMER role
    	Role customerRole = roleRepository.findByName(RoleType.CUSTOMER)
    	        .orElseThrow(() ->
    	                new RuntimeException("Default CUSTOMER role not found"));
        // Create User
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setGender(request.getGender());

        // Encrypt Password
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(customerRole);
        user.setEnabled(true);

        // Save User
        userRepository.save(user);

        return new AuthResponse(null, "Registration Successful");
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        // Authenticate User
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Get Logged-in User
        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        // Generate JWT
        String token = jwtUtil.generateToken(userDetails.getUsername());

        return new AuthResponse(token, "Login Successful");
    }
}