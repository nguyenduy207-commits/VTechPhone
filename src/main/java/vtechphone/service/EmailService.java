package vtechphone.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    // 🔥 CẬP NHẬT EMAIL VÀ MẬT KHẨU ỨNG DỤNG CỦA BẠN TẠI ĐÂY
    private static final String FROM = "nguyenduy2007nt@gmail.com";   
    private static final String PASSWORD = "uwfm regm nxnz erem";    
    
    public static void sendEmail(String to, String subject, String htmlContent) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM, PASSWORD);
            }
        });

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject);
            msg.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(msg);
            System.out.println("✅ Email đã gửi đến: " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("❌ Gửi email thất bại: " + e.getMessage());
        }
    }
}