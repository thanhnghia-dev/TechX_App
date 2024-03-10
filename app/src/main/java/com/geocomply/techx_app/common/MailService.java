package com.geocomply.techx_app.common;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class MailService {
    public static int sendOtp(String toEmail) {
        int otp = generateOTP();

        String fromEmail = "laptopnlu@gmail.com";
        String password = "ozskjhexbrxdtvdf";
        String subject = "Đặt lại mật khẩu";
        String body = "Xin chào," + "\n" + "Chào mừng bạn quay trở lại TechX. " +
                "Vui lòng nhập mã OTP này để đặt lại mật khẩu. OTP có hiệu lực trong vòng 1 phút: " + otp;

        // Mail server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Get the Session object
        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            message.setSubject(subject);
            message.setText(body);

            Thread thread = new Thread(() -> {
                try {
                    Transport.send(message);
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();

            return otp;
        } catch (MessagingException mex) {
            mex.printStackTrace();
            return 0;
        }
    }

    private static int generateOTP() {
        Random random = new Random();
        return random.nextInt(8999) + 1000;
    }
}
