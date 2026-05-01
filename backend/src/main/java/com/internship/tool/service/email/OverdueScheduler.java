package com.internship.tool.service.email;

import com.internship.tool.entity.ExampleEntity;
import com.internship.tool.repository.ExampleEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OverdueScheduler {
    @Autowired
    private ExampleEntityRepository repository;
    @Autowired
    private EmailService emailService;

    // Runs every day at 8 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendOverdueNotifications() {
        List<ExampleEntity> overdueEntities = repository.findAll() // Replace with actual overdue logic
                .stream()
                .filter(entity -> entity.getCreatedDate() != null && entity.getCreatedDate().isBefore(LocalDateTime.now().minusDays(7)))
                .toList();
        for (ExampleEntity entity : overdueEntities) {
            Map<String, Object> variables = new HashMap<>();
            variables.put("name", entity.getName());
            variables.put("description", entity.getDescription());
            variables.put("dueDate", entity.getCreatedDate());
            try {
                emailService.sendHtmlMessage("recipient@example.com", "Entity Overdue", "email/overdue", variables);
            } catch (MessagingException e) {
                // Log error
            }
        }
    }
}
