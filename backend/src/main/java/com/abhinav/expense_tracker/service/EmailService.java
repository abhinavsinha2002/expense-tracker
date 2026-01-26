package com.abhinav.expense_tracker.service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {
    @Autowired private JavaMailSender mailSender;
    @Value("${spring.mail.username}") 
    private String fromEmail;

    public void sendEmail(String to,String subject,String text){
        SimpleMailMessage msg=new SimpleMailMessage();
        msg.setFrom(fromEmail);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }
}
