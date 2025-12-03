package com.abhinav.expense_tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.abhinav.expense_tracker.entity.User;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUserName(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
