package com.abhinav.expense_tracker.service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {
    @Autowired private JavaMailSender mailSender;
    @Value("${spring.mail.username}") 
    private String fromEmail;

    public void sendEmail(String to,String name,String link){

        String htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Verify Your Account</title>
        </head>
        <body style="margin: 0; padding: 0; background-color: #f4f7f6; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;">
            
            <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%%" style="background-color: #f4f7f6; padding: 40px 0;">
                <tr>
                    <td align="center">
                        
                        <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="600" style="background-color: #ffffff; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); overflow: hidden;">
                            
                            <tr>
                                <td align="center" style="background-color: #2c3e50; padding: 30px 0;">
                                    <h1 style="color: #ffffff; margin: 0; font-size: 24px; letter-spacing: 1px;">HisabKitaab</h1>
                                </td>
                            </tr>

                            <tr>
                                <td style="padding: 40px 30px;">
                                    <h2 style="color: #2c3e50; font-size: 22px; margin-top: 0;">Welcome, %s! 👋</h2>
                                    <p style="color: #555555; font-size: 16px; line-height: 1.6; margin-bottom: 25px;">
                                        Thanks for signing up for HisabKitaab. We're excited to have you on board! 
                                        To get started with tracking your expenses, please verify your email address.
                                    </p>

                                    <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%%">
                                        <tr>
                                            <td align="center" style="padding: 10px 0 30px 0;">
                                                <a href="%s" target="_blank" style="display: inline-block; background-color: #27ae60; color: #ffffff; padding: 14px 30px; font-size: 16px; font-weight: bold; text-decoration: none; border-radius: 6px; box-shadow: 0 4px 6px rgba(39, 174, 96, 0.2);">
                                                    Verify My Account
                                                </a>
                                            </td>
                                        </tr>
                                    </table>

                                    <p style="color: #555555; font-size: 16px; line-height: 1.6;">
                                        This link will expire in 24 hours. If you did not create an account, no further action is required.
                                    </p>
                                    
                                    <br>
                                    <p style="color: #2c3e50; font-size: 16px; font-weight: bold;">Cheers,<br>The HisabKitaab Team</p>
                                </td>
                            </tr>

                            <tr>
                                <td align="center" style="background-color: #f8f9fa; padding: 20px; border-top: 1px solid #eeeeee;">
                                    <p style="color: #999999; font-size: 12px; margin: 0;">
                                        &copy; 2026 HisabKitaab Inc. All rights reserved.<br>
                                        <a href="#" style="color: #999999; text-decoration: underline;">Privacy Policy</a> | <a href="#" style="color: #999999; text-decoration: underline;">Support</a>
                                    </p>
                                </td>
                            </tr>

                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
    """.formatted(name, link); // Note: I used %% for CSS percentages to escape the format check

    try{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("Verify your HisabKitaab Account");
        helper.setText(htmlContent,true);
        mailSender.send(message);
    }
    catch(MessagingException e){

    }
}

}
