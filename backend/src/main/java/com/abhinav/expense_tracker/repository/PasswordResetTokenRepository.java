package com.abhinav.expense_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.abhinav.expense_tracker.entity.PasswordResetToken;
import com.abhinav.expense_tracker.entity.User;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken,Long> {
    PasswordResetToken findByToken(String token);
    PasswordResetToken findByUser(User user);
}
