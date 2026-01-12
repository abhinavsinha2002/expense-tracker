package com.abhinav.expense_tracker.service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EmailService {
    @Autowired private JavaMailSender mailSender;
    public void sendEmail(String to,String subject,String text){
        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }
}
