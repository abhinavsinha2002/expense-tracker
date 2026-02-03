package com.abhinav.expense_tracker.service;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.abhinav.expense_tracker.security.*;
import com.abhinav.expense_tracker.entity.User;
import com.abhinav.expense_tracker.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        User u=userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("Not found"));
        return UserPrincipal.create(u);
    }
}
