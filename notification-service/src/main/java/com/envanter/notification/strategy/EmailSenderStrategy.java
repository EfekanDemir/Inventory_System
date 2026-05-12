package com.envanter.notification.strategy;

import com.envanter.notification.dto.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * E-posta bildirim stratejisi — JavaMailSender ile entegre.
 *
 * <p>Gerçek SMTP zorunlu değildir: Spring Boot'un MockJavaMailSender'ı
 * veya Mailhog gibi bir test SMTP sunucusu yeterlidir.
 * Üretimde application.yml içindeki spring.mail.* ayarları devreye girer.</p>
 *
 * Single Responsibility: Yalnızca e-posta gönderiminden sorumludur.
 */
@Component
public class EmailSenderStrategy implements NotificationStrategy {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderStrategy.class);

    private final JavaMailSender mailSender;

    public EmailSenderStrategy(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(NotificationRequest request) {
        log.info("[EMAIL] Gönderiliyor → alıcı: {}, konu: {}",
                request.getRecipientEmail(), request.getSubject());

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(request.getRecipientEmail());
            message.setSubject(request.getSubject());
            message.setText(request.getContent());
            message.setFrom("noreply@envanter.com");

            mailSender.send(message);
            log.info("[EMAIL] Başarıyla gönderildi → {}", request.getRecipientEmail());

        } catch (Exception ex) {
            log.error("[EMAIL] Gönderim başarısız → alıcı: {}, hata: {}",
                    request.getRecipientEmail(), ex.getMessage());
            // Fırlatma: çağıran katman (service) status'u FAILED olarak kaydeder
            throw ex;
        }
    }

    @Override
    public String getSupportedType() {
        return "EMAIL";
    }
}
