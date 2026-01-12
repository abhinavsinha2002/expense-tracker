package com.abhinav.expense_tracker.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.abhinav.expense_tracker.entity.ExpenseGroup;
import com.abhinav.expense_tracker.entity.User;

public class NotificationService {
    @Autowired private GroupService groupService;
    @Autowired private EmailService emailService;

    @Scheduled(cron = "0 0 9 * * ?")
    public void dailyReminders(){
        List<ExpenseGroup> groups = groupService.findAll();
        for(ExpenseGroup g : groups){
            var txns = groupService.settleUp(g.getId());
            if(!txns.isEmpty()){
                for(User u : g.getMembers()){
                    emailService.sendEmail(u.getEmail(),"Pending settlements in group: "+ g.getName(),"Hi "+u.getUsername()+" .You have pending settlements. Visit your groups to settle up.");
                }
            }
        }
    }
}
