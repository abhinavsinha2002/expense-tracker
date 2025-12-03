package com.abhinav.expense_tracker.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.abhinav.expense_tracker.entity.Expense;
import com.abhinav.expense_tracker.entity.ExpenseGroup;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
    List<Expense> findByOwnerId(Long ownerId);
    List<Expense> findByDateBetween(LocalDate start,LocalDate end);
    List<Expense> findByGroup(ExpenseGroup group);
}
