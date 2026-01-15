package com.abhinav.expense_tracker.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class MessageController {
    
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/group")
    public Object sendMessage(Object msg){
        return msg;
    }
}
