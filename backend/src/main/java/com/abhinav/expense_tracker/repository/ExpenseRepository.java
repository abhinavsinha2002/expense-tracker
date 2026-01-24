package com.abhinav.expense_tracker.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.abhinav.expense_tracker.entity.Expense;
import com.abhinav.expense_tracker.entity.ExpenseGroup;

public interface ExpenseRepository extends JpaRepository<Expense,Long> {
    List<Expense> findByOwnerId(Long ownerId);
    List<Expense> findByDateBetween(LocalDate start,LocalDate end);
    List<Expense> findByGroup(ExpenseGroup group);

    @Query("SELECT DISTINCT e FROM Expense e LEFT JOIN e.splits s " +
           "WHERE e.owner.username = :username OR s.memberIdentifier = :username")
    List<Expense> findByUserInvolvement(@Param("username") String username);

    @Query("SELECT DISTINCT e FROM Expense e LEFT JOIN e.splits s " +
           "WHERE (e.date BETWEEN :start AND :end) " +
           "AND (e.owner.username = :username OR s.memberIdentifier = :username)")
    List<Expense> findByUserInvolvementAndDateBetween(@Param("start") LocalDate start, 
                                                      @Param("end") LocalDate end, 
                                                      @Param("username") String username);
}
