package com.abhinav.expense_tracker.service;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.abhinav.expense_tracker.entity.PasswordResetToken;
import com.abhinav.expense_tracker.entity.User;
import com.abhinav.expense_tracker.entity.VerificationToken;
import com.abhinav.expense_tracker.repository.PasswordResetTokenRepository;
import com.abhinav.expense_tracker.repository.UserRepository;
import com.abhinav.expense_tracker.repository.VerificationTokenRepository;
import com.abhinav.expense_tracker.security.JwtUtil;

@Service
public class AuthService {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private VerificationTokenRepository verificationTokenRepository;
    @Autowired private PasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired private EmailService emailService;
    @Autowired private JwtUtil jwtUtil;

    public User register(User user,String appUrl){
        if(userRepository.existsByUsername(user.getUsername())) throw new IllegalArgumentException("Username exists");
        if(userRepository.existsByEmail(user.getEmail())) throw new IllegalArgumentException("Email exists");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        User saved=userRepository.save(user);

        String token=UUID.randomUUID().toString();
        VerificationToken vt=new VerificationToken();
        vt.setToken(token);
        vt.setUser(saved);
        vt.setExpiryDate(new Date(System.currentTimeMillis()+24*3600*1000));
        verificationTokenRepository.save(vt);
        String link=appUrl+"/verify?token="+token;
        emailService.sendEmail(saved.getEmail(),"Verify your account","Click to verify"+link);
        return saved;
    }

    public boolean verifyAccount(String token){
        VerificationToken vt=verificationTokenRepository.findByToken(token);
        if(vt==null || vt.getExpiryDate().before(new Date())){
            return false;
        }
        User u = vt.getUser();
        u.setEnabled(true);
        userRepository.save(u);
        verificationTokenRepository.delete(vt);
        return true;
    }

    public void requestPasswordReset(String email, String appUrl){
        Optional<User> ou=userRepository.findByEmail(email);
        if(ou.isEmpty()){
            throw new IllegalArgumentException("User not found");
        }
        User u=ou.get();
        String token=UUID.randomUUID().toString();
        PasswordResetToken pr=new PasswordResetToken();
        pr.setToken(token);
        pr.setExpiryDate(new Date(System.currentTimeMillis()+30*60*1000));
        pr.setUser(u);
        passwordResetTokenRepository.save(pr);
        String link=appUrl+"/reset?token="+token;
        emailService.sendEmail(u.getEmail(),"Password reset","Reset link: "+link);
    }

    public void resetPassword(String token,String newPassword){
        PasswordResetToken pr=passwordResetTokenRepository.findByToken(token);
        if(pr==null || pr.getExpiryDate().before(new Date())){
            throw new IllegalArgumentException("Invalid token");
        }
        User u=pr.getUser();
        u.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(u);
        passwordResetTokenRepository.delete(pr);
    }

}
