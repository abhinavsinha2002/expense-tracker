package com.abhinav.expense_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.abhinav.expense_tracker.entity.ExpenseGroup;

public interface GroupRepository extends JpaRepository<ExpenseGroup,Long> {
}
