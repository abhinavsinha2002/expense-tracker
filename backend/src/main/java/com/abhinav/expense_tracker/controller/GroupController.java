package com.abhinav.expense_tracker.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abhinav.expense_tracker.dto.ExpenseResponseDto;
import com.abhinav.expense_tracker.dto.GroupCreateDto;
import com.abhinav.expense_tracker.dto.GroupResponseDto;
import com.abhinav.expense_tracker.dto.SettleUpTransactionDto;
import com.abhinav.expense_tracker.entity.Expense;
import com.abhinav.expense_tracker.entity.ExpenseGroup;
import com.abhinav.expense_tracker.security.JwtUtil;
import com.abhinav.expense_tracker.service.GroupService;
import com.abhinav.expense_tracker.util.DtoMapper;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    @Autowired private GroupService groupService;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<GroupResponseDto> createGroup(@RequestBody GroupCreateDto dto,Authentication auth){
        ExpenseGroup g = groupService.createGroup(dto.getName(), dto.getCurrency(),auth.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toGroupDto(g));
    }

    @PostMapping("/join/{token}")
    public ResponseEntity<?> joinGroup(@PathVariable String token, Authentication auth){
        String email = auth.getName();
        groupService.joinGroup(token, email);
        return ResponseEntity.ok("Joined group successfully");
    }

    @GetMapping("/{id}/settle")
    public ResponseEntity<List<SettleUpTransactionDto>> settle(@PathVariable Long id){
        return ResponseEntity.ok(groupService.settleUp(id));
    }

    @GetMapping
    public ResponseEntity<List<GroupResponseDto>> listAllGroups(Authentication auth){
        List<ExpenseGroup> groups = groupService.findGroupsForUser(auth.getName());
        List<GroupResponseDto> dtos = groups.stream().map(DtoMapper::toGroupDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDto> getGroup(@PathVariable Long id, Authentication auth){
        ExpenseGroup g = groupService.getGroup(id, auth.getName());
        return ResponseEntity.ok(DtoMapper.toGroupDto(g));
    }

    @GetMapping("/{id}/expenses")
    public ResponseEntity<List<ExpenseResponseDto>> getGroupExpenses(@PathVariable Long id, Authentication auth){
        List<Expense> expenses = groupService.getGroupExpenses(id, auth.getName());
        List<ExpenseResponseDto> dtos = expenses.stream()
                                        .map(DtoMapper::toExpenseDto)
                                        .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    
}
