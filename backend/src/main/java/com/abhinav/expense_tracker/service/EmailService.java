package com.abhinav.expense_tracker.service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;

@Service
public class EmailService {
    @Autowired private JavaMailSender mailSender;
    @Value("${spring.mail.username}") 
    private String fromEmail;

    public void sendEmail(String to,String name,String link){

        String subject = "Verify your HisabKitaab Account";
        String title = "Welcome, " + name + "!";
        String body = "You've taken the first step to mastering your wealth. To secure your account and start tracking your expenses, please verify your email address.";
        String btnText = "Verify Account";
        
        sendHtmlEmail(to, subject, title, body, btnText, link);
    }

    public void sendPasswordResetEmail(String to, String name, String link) {
        String subject = "Reset Your Password";
        String title = "Password Reset Request";
        String body = "We received a request to reset the password for your HisabKitaab account. If you didn't make this request, you can safely ignore this email.";
        String btnText = "Reset Password";
        
        sendHtmlEmail(to, subject, title, body, btnText, link);
    }

    public void sendVerificationWarningEmail(String to, String name, String link) {
        String subject = "Action Required: Verify Account First";
        String title = "Verify to Reset Password";
        String body = "You requested a password reset, but your account is not verified yet. For security reasons, you must verify your email address before you can reset your password.";
        String btnText = "Verify Now";

        sendHtmlEmail(to, subject, title, body, btnText, link);
    }

    private void sendHtmlEmail(String to, String subject, String title, String body, String btnText, String link) {
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
                <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
                <style>
                    body, table, td, a { -webkit-text-size-adjust: 100%%; -ms-text-size-adjust: 100%%; }
                    table, td { mso-table-lspace: 0pt; mso-table-rspace: 0pt; }
                    img { -ms-interpolation-mode: bicubic; border: 0; height: auto; line-height: 100%%; outline: none; text-decoration: none; }
                    table { border-collapse: collapse !important; }
                    body { height: 100%% !important; margin: 0 !important; padding: 0 !important; width: 100%% !important; font-family: 'Poppins', sans-serif; }
                    .button-link:hover { background-color: #dcb14a !important; color: #000000 !important; box-shadow: 0 8px 25px rgba(220, 177, 74, 0.4) !important; transform: translateY(-2px); }
                </style>
            </head>
            <body style="margin: 0; padding: 0; background-color: #f4f6f8;">
                <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="100%%">
                    <tr>
                        <td align="center" style="padding: 40px 10px;">
                            <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="600" style="background-color: #ffffff; border-radius: 15px; box-shadow: 0 10px 40px rgba(0,0,0,0.08); overflow: hidden; max-width: 600px;">
                                <tr>
                                    <td align="center" style="padding: 40px 0 20px 0; background-color: #ffffff;">
                                        <h1 style="margin: 0; font-family: 'Poppins', sans-serif; font-size: 28px; font-weight: 700; color: #1a1a1a; letter-spacing: 2px;">
                                            HISAB<span style="color: #dcb14a;">KITAB</span>
                                        </h1>
                                        <p style="margin: 5px 0 0 0; font-family: 'Poppins', sans-serif; font-size: 10px; color: #888888; text-transform: uppercase; letter-spacing: 3px; font-weight: 500;">
                                            Premium Expense Tracking
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding: 0 20px;">
                                        <img src="cid:cycle.gif" alt="Expense Cycle" width="180" style="display: block; width: 180px; max-width: 100%%; border-radius: 8px;">
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="padding: 30px 40px 50px 40px;">
                                        <h2 style="margin: 0 0 20px 0; font-family: 'Poppins', sans-serif; font-size: 24px; font-weight: 600; color: #1a1a1a;">
                                            %s
                                        </h2>
                                        <p style="margin: 0 0 30px 0; font-family: 'Poppins', sans-serif; font-size: 15px; line-height: 1.8; color: #555555;">
                                            %s
                                        </p>
                                        <table role="presentation" border="0" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td align="center" style="border-radius: 50px; background-color: #1a1a1a; background: linear-gradient(135deg, #1a1a1a 0%%, #333333 100%%); box-shadow: 0 4px 15px rgba(0,0,0,0.2);">
                                                    <a href="%s" class="button-link" style="display: inline-block; padding: 16px 36px; font-family: 'Poppins', sans-serif; font-size: 14px; font-weight: 600; color: #dcb14a; text-decoration: none; text-transform: uppercase; letter-spacing: 1.5px; border-radius: 50px; transition: all 0.3s;">
                                                        %s
                                                    </a>
                                                </td>
                                            </tr>
                                        </table>
                                        <p style="margin: 40px 0 0 0; font-family: 'Poppins', sans-serif; font-size: 13px; color: #999999; line-height: 1.6;">
                                            This link is valid for <strong>24 hours</strong>.<br>
                                            If you didn't request this, you can safely ignore this email.
                                        </p>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" style="background-color: #f8f9fa; padding: 30px; border-top: 1px solid #eeeeee;">
                                        <p style="margin: 0; font-family: 'Poppins', sans-serif; font-size: 12px; color: #aaaaaa;">
                                            &copy; 2026 HisabKitaab Inc. All rights reserved.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
        """.formatted(subject, title, body, link, btnText);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            
            // Fix: Set text BEFORE adding inline image
            helper.setText(htmlContent, true);

            ClassPathResource image = new ClassPathResource("static/cycle.gif");
            if(image.exists()){
                helper.addInline("cycle.gif", image);
            }
            
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


}
