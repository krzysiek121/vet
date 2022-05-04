package pl.kurs.vet.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.vet.model.EMail;
import pl.kurs.vet.service.EmailSenderService;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/email/")
@RequiredArgsConstructor
public class EmailSenderController {
    private final EmailSenderService emailSenderService;

    @PostMapping("/email/send/html")
    public void sendHtmlMessage(@RequestBody EMail email) throws MessagingException {
        emailSenderService.sendHtmlMessage(email);
    }
}
