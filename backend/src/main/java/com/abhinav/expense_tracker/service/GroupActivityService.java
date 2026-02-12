package com.abhinav.expense_tracker.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import com.abhinav.expense_tracker.dto.ActivityDTO;
import com.abhinav.expense_tracker.entity.Expense;
import com.abhinav.expense_tracker.entity.ExpenseGroup;
import com.abhinav.expense_tracker.entity.GroupActivity;
import com.abhinav.expense_tracker.entity.User;
import com.abhinav.expense_tracker.enums.ActivityType;
import com.abhinav.expense_tracker.repository.GroupActivityRepository;

@Service
public class GroupActivityService {
    @Autowired private GroupActivityRepository activityRepository;

    public void logChat(User user, ExpenseGroup group, String message){
        saveActivity(user,group,ActivityType.CHAT,message,null,null,null);
    }

    public void logExpenseAdded(User user, ExpenseGroup group, Expense expense){
        String msg = "added '"+expense.getDescription()+" ' ";
        saveActivity(user,group,ActivityType.EXPENSE_ADDED,msg,expense.getId(),expense.getDescription(),null);
    }

    public void logExpenseDeleted(User user, ExpenseGroup group, Expense expense){
        String msg = "deleted '"+expense.getDescription()+ "' ("+expense.getAmount()+ ")";
        saveActivity(user, group, ActivityType.EXPENSE_DELETED, msg,null,expense.getDescription(),null);
    }

    public void logExpenseUpdated(User user, ExpenseGroup group, Expense oldExp,Expense newExp ){
        StringBuilder changes = new StringBuilder();

        if(!oldExp.getAmount().equals(newExp.getAmount())){
            changes.append("Amount: ").append(oldExp.getAmount()).append(" -> ").append(newExp.getAmount()).append(". ");
        }
        if(!oldExp.getDescription().equals(newExp.getDescription())){
            changes.append("Name: '").append(oldExp.getDescription()).append("' -> '").append(newExp.getDescription()).append("'.");
        }

        if(changes.length()>0){
            String msg = "updated '"+newExp.getDescription()+"'";
            saveActivity(user,group,ActivityType.EXPENSE_UPDATED,msg,newExp.getId(),newExp.getDescription(),changes.toString());
        }
    }

    private void saveActivity(User user, ExpenseGroup group,ActivityType type,String message, Long entityId, String entityName, String changeDetails){
        GroupActivity activity = new GroupActivity();
        activity.setUser(user);
        activity.setGroup(group);
        activity.setType(type);
        activity.setMessage(message);
        activity.setEntityId(entityId);
        activity.setEntityName(entityName);
        activity.setChangeDetails(changeDetails);
        activity.setTimestamp(LocalDateTime.now());
        activityRepository.save(activity);
    }

    public List<ActivityDTO> getGroupActivities(Long groupId){
        List<GroupActivity> logs = activityRepository.findByGroupIdOrderByTimestampAsc(groupId);

        return logs.stream().map(log->{
            ActivityDTO dto = new ActivityDTO();
            dto.setId(log.getId());
            dto.setType(log.getType().name());
            dto.setMessage(log.getMessage());
            dto.setUserId(log.getUser().getId());
            dto.setUserName(log.getUser().getFullName());
            dto.setEntityId(log.getEntityId());
            dto.setChangeDetails(log.getChangeDetails());
            dto.setTimestamp(log.getTimestamp());
            return dto;
        }).collect(Collectors.toList());
    }
}
