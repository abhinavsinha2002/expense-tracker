package com.abhinav.expense_tracker.controller;

import java.util.List;

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
import com.abhinav.expense_tracker.dto.SettleUpTransactionDto;
import com.abhinav.expense_tracker.entity.ExpenseGroup;
import com.abhinav.expense_tracker.service.GroupService;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins="http://localhost:4200")
public class GroupController {
    @Autowired private GroupService groupService;

    @PostMapping
    public ResponseEntity<ExpenseGroup> createGroup(@RequestBody GroupCreateDto dto,Authentication auth){
        ExpenseGroup g = groupService.createGroup(dto.getName(), auth.getName(), dto.getMembers());
        return ResponseEntity.status(HttpStatus.CREATED).body(g);
    }

    @GetMapping("/{id}/settle")
    public ResponseEntity<List<SettleUpTransactionDto>> settle(@PathVariable Long id){
        return ResponseEntity.ok(groupService.settleUp(id));
    }

    @GetMapping
    public ResponseEntity<List<ExpenseGroup>> listAllGroups(Authentication auth){
        return ResponseEntity.ok(groupService.findGroupsForUser(auth.getName()));
    }

    
}
