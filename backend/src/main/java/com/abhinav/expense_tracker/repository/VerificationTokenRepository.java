package com.abhinav.expense_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.abhinav.expense_tracker.entity.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {
    VerificationToken findByToken(String token);
}
