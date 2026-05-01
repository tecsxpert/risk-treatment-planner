package com.internship.tool.service;

import com.internship.tool.entity.ExampleEntity;
import com.internship.tool.repository.ExampleEntityRepository;
import com.internship.tool.exception.EntityNotFoundException;
import com.internship.tool.exception.InvalidInputException;
import org.springframework.beans.factory.annotation.Autowired;
import com.internship.tool.service.email.EmailService;
import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;
import java.util.Optional;

@Service
public class ExampleEntityService {
    private final ExampleEntityRepository repository;
    private final EmailService emailService;

    @Autowired
    @Autowired
    public ExampleEntityService(ExampleEntityRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }


    @Cacheable(value = "exampleEntities", key = "#pageable", unless = "#result == null")
    public org.springframework.data.domain.Page<ExampleEntity> getAllPaginated(org.springframework.data.domain.Pageable pageable) {
        return repository.findAll(pageable);
    }


    @Cacheable(value = "exampleEntitiesAll", unless = "#result == null")
    public List<ExampleEntity> getAll() {
        return repository.findAll();
    }


    @Cacheable(value = "exampleEntity", key = "#id", unless = "#result == null")
    public ExampleEntity getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));
    }


    @Transactional
    @CacheEvict(value = {"exampleEntities", "exampleEntitiesAll", "exampleEntity"}, allEntries = true)
    public ExampleEntity create(ExampleEntity entity) {
        validate(entity);
        ExampleEntity saved = repository.save(entity);
        // Send email notification on create
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", saved.getName());
        variables.put("description", saved.getDescription());
        variables.put("date", saved.getCreatedDate());
        try {
            emailService.sendHtmlMessage("recipient@example.com", "Entity Created", "email/created", variables);
        } catch (MessagingException e) {
            // Log error
        }
        return saved;
    }


    @Transactional
    @CacheEvict(value = {"exampleEntities", "exampleEntitiesAll", "exampleEntity"}, allEntries = true)
    public ExampleEntity update(Long id, ExampleEntity entity) {
        validate(entity);
        ExampleEntity existing = getById(id);
        existing.setName(entity.getName());
        existing.setDescription(entity.getDescription());
        return repository.save(existing);
    }


    @Transactional
    @CacheEvict(value = {"exampleEntities", "exampleEntitiesAll", "exampleEntity"}, allEntries = true)
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Entity not found with id: " + id);
        }
        repository.deleteById(id);
    }

    private void validate(ExampleEntity entity) {
        if (entity.getName() == null || entity.getName().trim().isEmpty()) {
            throw new InvalidInputException("Name must not be empty");
        }
        // Add more validation as needed
    }
}
