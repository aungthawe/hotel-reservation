package com.project.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ContactController {

    @Autowired
    private JavaMailSender mailSender;

    @PostMapping("/contact/send")
    public String sendContactMessage(@RequestParam String name,
                                     @RequestParam String email,
                                     @RequestParam String message,
                                     RedirectAttributes redirectAttributes) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo("your-email@example.com"); // change this to your email
            mailMessage.setSubject("Contact Form Message from " + name);
            mailMessage.setText("Email: " + email + "\n\n" + message);

            mailSender.send(mailMessage);
            redirectAttributes.addFlashAttribute("message", "Message sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to send message.");
            e.printStackTrace();
        }

        return "redirect:/contact";
    }
}
