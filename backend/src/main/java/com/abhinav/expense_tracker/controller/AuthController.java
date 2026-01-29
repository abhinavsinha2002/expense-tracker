package com.abhinav.expense_tracker.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import com.abhinav.expense_tracker.dto.AuthRequest;
import com.abhinav.expense_tracker.dto.AuthResponse;
import com.abhinav.expense_tracker.entity.User;
import com.abhinav.expense_tracker.repository.UserRepository;
import com.abhinav.expense_tracker.security.JwtUtil;
import com.abhinav.expense_tracker.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired private AuthService authService;
    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user,HttpServletRequest req){
        String appUrl = "http://localhost:4200";
        authService.register(user, appUrl);
        return ResponseEntity.ok("Registered - check your email for verification");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token){
        boolean ok = authService.verifyAccount(token);
        if(!ok){
            return ResponseEntity.badRequest().body("Invalid/expired token");
        }
        return ResponseEntity.ok("Account verified");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest r){
        try{
            authManager.authenticate(new UsernamePasswordAuthenticationToken(r.getUsername(),r.getPassword()));
        }
        catch(BadCredentialsException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Bad credentials");
        }
        String token = jwtUtil.generateToken(r.getUsername());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/reset/request")
    public ResponseEntity<String> requestReset(@RequestParam String email,HttpServletRequest req){
        String appUrl = "http://localhost:4200";
        authService.requestPasswordReset(email, appUrl);
        return ResponseEntity.ok("Reset link sent to email");
    }

    @PostMapping("/reset/confirm")
    public ResponseEntity<String> resetConfirm(@RequestParam String token,@RequestParam String newPassword){
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password changed");
    }
    
    @GetMapping("/check-availability")
    public ResponseEntity<?> checkAvailability(@RequestParam String field,@RequestParam String value){
        boolean exists = false;

        if (field == null || value == null) {
            return ResponseEntity.badRequest().body("Field and value are required");
        }
        if("username".equals(field)){
            exists = userRepository.existsByUsername(value);
        }
        else if("email".equals(field)){
            exists = userRepository.existsByEmail(value);
        }
        return ResponseEntity.ok(Collections.singletonMap("available", !exists));
    }
}
