package com.geocomply.techx_app.common;

import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class MailService {
    public static int sendOtp(String toEmail) {
        int otp = generateOTP();

        String fromName = "NLU Laptop";
        String fromEmail = "laptopnlu@gmail.com";
        String password = "ozskjhexbrxdtvdf";
        String subject = "Đặt lại mật khẩu";
        String body = "<html><body>Xin chào,<br/>Chào mừng bạn quay trở lại TechX. " +
                "Vui lòng nhập mã OTP này để đặt lại mật khẩu. OTP có hiệu lực trong vòng 1 phút: " +
                "<span style='font-size: 18px;'><b>" + otp + "</b></span></body></html>";

        // Mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.from", fromEmail);
        properties.put("mail.smtp.fromname", fromName);

        // Get the Session object
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail, fromName));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject, "UTF-8");
            message.setContent(body, "text/html; charset=utf-8");

            Thread thread = new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();

            return otp;
        } catch (MessagingException | UnsupportedEncodingException mex) {
            mex.printStackTrace();
            return 0;
        }
    }

    private static int generateOTP() {
        Random random = new Random();
        return random.nextInt(8999) + 1000;
    }

    private static String makeBold(String text) {
        return "\033[1m" + text + "\033[0m";
    }
}
