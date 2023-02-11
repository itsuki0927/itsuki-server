package cn.itsuki.blog.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import java.util.List;

/**
 * 邮箱 服务
 */
@Service
public class EmailService {

    @Value("${mail.from}")
    private String from;
    @Autowired
    private JavaMailSender mailSender;

    /**
     * 发送邮箱
     *  @param subject 主题
     * @param text    内容
     * @param to      收件人地址
     */
    public void sendEmail(String subject, String text, List<String> to) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            mimeMessage.setSubject(subject);
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            helper.setFrom(from);
            helper.setTo(to.toArray(new String[]{}));
            helper.setText(text, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            throw new RuntimeException("Cannot send email", ex);
        }
    }
}
