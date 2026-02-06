package com.abhinav.expense_tracker.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.abhinav.expense_tracker.dto.RegisterRequestUser;
import com.abhinav.expense_tracker.entity.AuthProvider;
import com.abhinav.expense_tracker.entity.PasswordResetToken;
import com.abhinav.expense_tracker.entity.User;
import com.abhinav.expense_tracker.entity.VerificationToken;
import com.abhinav.expense_tracker.repository.PasswordResetTokenRepository;
import com.abhinav.expense_tracker.repository.UserRepository;
import com.abhinav.expense_tracker.repository.VerificationTokenRepository;
import com.abhinav.expense_tracker.security.JwtUtil;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil jwtUtil;

    public User register(RegisterRequestUser request, String appUrl) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user != null) {
            if (user.isEnabled()) {
                throw new IllegalArgumentException("Email already exists. Please login.");
            }

            user.setFullName(request.getFullName());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        } else {
            user = new User();
            user.setFullName(request.getFullName());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setAuthProvider(AuthProvider.LOCAL);
            user.setEnabled(false);

            String baseHandle = request.getEmail().split("@")[0];
            String finalHandle = baseHandle;
            int suffix = 1;

            while (userRepository.existsByUsername(finalHandle)) {
                finalHandle = baseHandle + suffix;
                suffix++;
            }
            user.setUsername(finalHandle);
        }

        User saved = userRepository.save(user);

        String token = UUID.randomUUID().toString();
        VerificationToken vt = verificationTokenRepository.findByUser(saved);

        if(vt==null){
            vt = new VerificationToken();
            vt.setUser(saved);
        }
        vt.setToken(token);

        vt.setExpiryDate(new Date(System.currentTimeMillis()+24*3600*1000));
        verificationTokenRepository.save(vt);
        String link = appUrl + "/verify?token=" + token;
        emailService.sendEmail(saved.getEmail(), saved.getFullName(), link);
        return saved;
    }

    public boolean verifyAccount(String token) {
        VerificationToken vt = verificationTokenRepository.findByToken(token);
        if (vt == null || vt.getExpiryDate().before(new Date())) {
            return false;
        }
        User u = vt.getUser();
        u.setEnabled(true);
        userRepository.save(u);
        verificationTokenRepository.delete(vt);
        return true;
    }

    public void requestPasswordReset(String email, String appUrl) {
        Optional<User> ou = userRepository.findByEmail(email);
        if (ou.isEmpty()) {
            return;
        }
        User u = ou.get();
        if(!u.isEnabled()){
            String token = UUID.randomUUID().toString();
            VerificationToken vt = verificationTokenRepository.findByUser(u);
            if(vt == null){
                vt = new VerificationToken();
                vt.setUser(u);
            }
            vt.setToken(token);
            vt.setExpiryDate(new Date(System.currentTimeMillis()+24*3600*1000));
            verificationTokenRepository.save(vt);

            String verifyLink = appUrl+"/verify?token="+token;
            emailService.sendVerificationWarningEmail(u.getEmail(), u.getFullName(), verifyLink);
            return;
        }
        String token = UUID.randomUUID().toString();
        PasswordResetToken pr = passwordResetTokenRepository.findByUser(u);
        if(pr == null){
            pr = new PasswordResetToken();
            pr.setUser(u);
        }
        pr.setToken(token);
        pr.setExpiryDate(new Date(System.currentTimeMillis() + 30 * 60 * 1000));
        passwordResetTokenRepository.save(pr);
        String link = appUrl + "/reset?token=" + token;
        emailService.sendPasswordResetEmail(u.getEmail(), u.getFullName(), link);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken pr = passwordResetTokenRepository.findByToken(token);
        if (pr == null || pr.getExpiryDate().before(new Date())) {
            throw new IllegalArgumentException("Invalid token");
        }
        User u = pr.getUser();
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(u);
        passwordResetTokenRepository.delete(pr);
    }

}
