package com.abhinav.expense_tracker.util;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.abhinav.expense_tracker.dto.ExpenseResponseDto;
import com.abhinav.expense_tracker.dto.ExpenseSplitDto;
import com.abhinav.expense_tracker.dto.GroupResponseDto;
import com.abhinav.expense_tracker.dto.UserDto;
import com.abhinav.expense_tracker.entity.Expense;
import com.abhinav.expense_tracker.entity.ExpenseGroup;
import com.abhinav.expense_tracker.entity.ExpenseSplit;
import com.abhinav.expense_tracker.entity.User;

public class DtoMapper {
    
    public static UserDto toUserDto(User user){
        if(user==null){
            return null;
        }
        return new UserDto(user.getId(),user.getUsername(),user.getFullName(),user.getEmail());
    }

    public static GroupResponseDto toGroupDto(ExpenseGroup group){
        return new GroupResponseDto(group.getId(), group.getName(), toUserDto(group.getOwner()), 
        group.getMembers().stream().map(DtoMapper::toUserDto).collect(Collectors.toSet()),group.getCurrency());
    }

    public static ExpenseResponseDto toExpenseDto(Expense expense){
        ExpenseResponseDto dto = new ExpenseResponseDto();
        dto.setId(expense.getId());
        dto.setDescription(expense.getDescription());
        dto.setAmount(expense.getAmount());
        dto.setDate(expense.getDate());
        dto.setOwner(toUserDto(expense.getOwner()));
        if(expense.getCategory()!=null){
            dto.setCategory(expense.getCategory().getName());
        }
        if(expense.getGroup()!=null){
            dto.setGroupName(expense.getGroup().getName());
        }

        if(expense.getSplits()!=null && !expense.getSplits().isEmpty()){
            dto.setSplits(expense.getSplits().stream().map(DtoMapper::toSplitDto).collect(Collectors.toList()));
        }
        else{
            dto.setSplits(new ArrayList<>());
        }

        return dto;
    }

    public static ExpenseSplitDto toSplitDto(ExpenseSplit split){
        ExpenseSplitDto dto = new ExpenseSplitDto();
        dto.setMember(split.getMemberIdentifier());
        dto.setAmount(split.getAmount());
        return dto;
    }

}
