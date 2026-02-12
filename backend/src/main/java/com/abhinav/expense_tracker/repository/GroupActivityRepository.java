package com.abhinav.expense_tracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abhinav.expense_tracker.entity.GroupActivity;

@Repository
public interface GroupActivityRepository extends JpaRepository<GroupActivity,Long> {
    List<GroupActivity> findByGroupIdOrderByTimestampAsc(Long groupId);
}
