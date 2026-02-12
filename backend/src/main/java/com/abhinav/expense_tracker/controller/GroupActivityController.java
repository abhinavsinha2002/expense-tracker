package com.abhinav.expense_tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.abhinav.expense_tracker.dto.ActivityDTO;
import com.abhinav.expense_tracker.entity.ExpenseGroup;
import com.abhinav.expense_tracker.entity.User;
import com.abhinav.expense_tracker.repository.GroupRepository;
import com.abhinav.expense_tracker.repository.UserRepository;
import com.abhinav.expense_tracker.service.GroupActivityService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups/{groupId}/activities")
public class GroupActivityController {
    
    @Autowired private GroupActivityService activityService;
    @Autowired private GroupRepository groupRepository;
    @Autowired private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<ActivityDTO>> getGroupHistory(@PathVariable Long groupId, Authentication auth){
        return ResponseEntity.ok(activityService.getGroupActivities(groupId));
    }

    @PostMapping("/chat")
    public ResponseEntity<?> sendChatMessage(@PathVariable Long groupId, 
                                             @RequestBody Map<String, String> payload, 
                                             Authentication auth) {
        
        String message = payload.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Message cannot be empty");
        }

        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        activityService.logChat(user, group, message);

        return ResponseEntity.ok("Message sent");
    }
}
