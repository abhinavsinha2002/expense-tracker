package com.abhinav.expense_tracker.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.abhinav.expense_tracker.entity.ExpenseGroup;

public interface GroupRepository extends JpaRepository<ExpenseGroup,Long> {
    Optional<ExpenseGroup> findByInviteToken(String inviteToken);
}
