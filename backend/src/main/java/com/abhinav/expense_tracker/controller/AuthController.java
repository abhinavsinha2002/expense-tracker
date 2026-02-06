package com.abhinav.expense_tracker.controller;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.abhinav.expense_tracker.dto.AuthRequest;
import com.abhinav.expense_tracker.dto.AuthResponse;
import com.abhinav.expense_tracker.dto.RegisterRequestUser;
import com.abhinav.expense_tracker.dto.UserDto;
import com.abhinav.expense_tracker.entity.AuthProvider;
import com.abhinav.expense_tracker.entity.User;
import com.abhinav.expense_tracker.repository.UserRepository;
import com.abhinav.expense_tracker.security.JwtUtil;
import com.abhinav.expense_tracker.security.UserPrincipal;
import com.abhinav.expense_tracker.service.AuthService;
import com.abhinav.expense_tracker.service.CustomUserDetailsService;
import com.abhinav.expense_tracker.util.DtoMapper;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired private AuthService authService;
    @Autowired private AuthenticationManager authManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestUser request,HttpServletRequest req){
        String appUrl = "http://localhost:4200";
        authService.register(request, appUrl);
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
            authManager.authenticate(new UsernamePasswordAuthenticationToken(r.getEmail(),r.getPassword()));
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(r.getEmail());
            String token = jwtUtil.generateToken(userDetails.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        }
        catch(BadCredentialsException ex){
            var userOpt = userRepository.findByEmail(r.getEmail());
            if(userOpt.isPresent()){
                User user = userOpt.get();
                if(user.getAuthProvider() == AuthProvider.GOOGLE){
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("You previously logged in via Google. Please continue with Google or reset your password.");
                }
                else if(user.getAuthProvider()==AuthProvider.GITHUB){
                     return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("You previously logged in via Github. Please continue with Github or reset your password.");
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }    
    }

    @PostMapping("/reset/request")
    public ResponseEntity<?> requestReset(@RequestParam String email,HttpServletRequest req){
        String appUrl = "http://localhost:4200";
        authService.requestPasswordReset(email, appUrl);
        return ResponseEntity.ok(Collections.singletonMap("message", 
            "If an account exists, a reset link has been sent to your email."));
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
        if("email".equals(field)){
            Optional<User> userOpt = userRepository.findByEmail(value);
            if(userOpt.isPresent()){
                User user = userOpt.get();
                if(user.isEnabled()){
                    exists = true;
                }
            }
        }
        return ResponseEntity.ok(Collections.singletonMap("available", !exists));
    }

    @GetMapping("/user/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal){
        if(userPrincipal == null){
            return ResponseEntity.notFound().build();
        }
        String email = userPrincipal.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("User not found"));
        return ResponseEntity.ok(DtoMapper.toUserDto(user));
    }

}
