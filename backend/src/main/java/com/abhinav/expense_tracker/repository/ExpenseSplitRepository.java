package com.abhinav.expense_tracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.abhinav.expense_tracker.entity.ExpenseSplit;

public interface ExpenseSplitRepository extends JpaRepository<ExpenseSplit,Long> {

}
