package pl.kurs.vet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import pl.kurs.vet.model.EMail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    public void sendHtmlMessage(EMail email) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(email.getProperties());
        helper.setFrom(email.getFrom());
        helper.setTo(email.getTo());
        helper.setSubject(email.getSubject());
        String html = templateEngine.process(email.getTemplate(), context);
        helper.setText(html, true);

        emailSender.send(message);
    }
    @Async
    public void sendConfirmationToEmail(String PatientEmail, String data, String name, String link) throws MessagingException {
        EMail email = new EMail();
        email.setTo(PatientEmail);
        email.setFrom("awandaliak@gmail.com");
        email.setSubject("Confirmation email from VetClinic");
        email.setTemplate("welcome-email.html");
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", name);
        properties.put("bookdate", data);
        properties.put("link", link);
        email.setProperties(properties);

        sendHtmlMessage(email);
    }
}