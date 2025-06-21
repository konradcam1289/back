package com.workshopapp.workshopservice.controller.publi;

import com.workshopapp.workshopservice.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "http://localhost:5173")
public class ContactController {

    private final JavaMailSender mailSender;

    @Value("${contact.receiver.email}") // np. email workera z application.properties
    private String receiverEmail;

    public ContactController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostMapping
    public ResponseEntity<String> sendContactMessage(@RequestBody ContactRequest contact) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(receiverEmail);
            message.setSubject("Wiadomość z formularza kontaktowego: " + contact.getSubject());
            message.setText("Od: " + contact.getName() + "\nEmail: " + contact.getEmail() + "\n\n" + contact.getMessage());

            mailSender.send(message);
            return ResponseEntity.ok("Wiadomość wysłana pomyślnie.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Błąd podczas wysyłania wiadomości.");
        }
    }
}

