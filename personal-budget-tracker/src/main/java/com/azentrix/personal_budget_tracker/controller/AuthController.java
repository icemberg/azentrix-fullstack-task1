package com.azentrix.personal_budget_tracker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azentrix.personal_budget_tracker.dto.ApiResponse;
import com.azentrix.personal_budget_tracker.dto.AuthRequest;
import com.azentrix.personal_budget_tracker.dto.AuthResponse;
import com.azentrix.personal_budget_tracker.entity.User;
import com.azentrix.personal_budget_tracker.enums.ResponseMessage;
import com.azentrix.personal_budget_tracker.repository.interfaces.UserRepository;
import com.azentrix.personal_budget_tracker.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(ResponseMessage.USER_ALREADY_EXISTS));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ResponseMessage.USER_REGISTERED, authResponse));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(ResponseMessage.UNAUTHORIZED));
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        AuthResponse authResponse = AuthResponse.builder()
                .token(token)
                .username(request.getUsername())
                .build();

        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.LOGIN_SUCCESS, authResponse));
    }
}
