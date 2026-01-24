package com.abhinav.expense_tracker.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.abhinav.expense_tracker.dto.ChatMessage;

@Controller
public class MessageController {
    
    @MessageMapping("/chat/{groupId}")
    @SendTo("/topic/group/{groupId}")
    public ChatMessage sendMessage(@DestinationVariable String groupId,ChatMessage msg ){
        return msg;
    }
}
