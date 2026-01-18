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

import com.abhinav.expense_tracker.dto.GroupCreateDto;
import com.abhinav.expense_tracker.dto.GroupResponseDto;
import com.abhinav.expense_tracker.dto.SettleUpTransactionDto;
import com.abhinav.expense_tracker.entity.ExpenseGroup;
import com.abhinav.expense_tracker.service.GroupService;
import com.abhinav.expense_tracker.util.DtoMapper;

@RestController
@RequestMapping("/api/groups")
public class GroupController {
    @Autowired private GroupService groupService;

    @PostMapping
    public ResponseEntity<GroupResponseDto> createGroup(@RequestBody GroupCreateDto dto,Authentication auth){
        ExpenseGroup g = groupService.createGroup(dto.getName(), auth.getName(), dto.getMembers());
        return ResponseEntity.status(HttpStatus.CREATED).body(DtoMapper.toGroupDto(g));
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

    
}
