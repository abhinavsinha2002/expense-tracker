package com.abhinav.expense_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.abhinav.expense_tracker.entity.PasswordResetToken;

public interface PasswordResetTokenRepositor extends JpaRepository<PasswordResetToken,Long> {
    PasswordResetToken findByToken(String token);
}
