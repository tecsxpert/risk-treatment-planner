package com.internship.tool.controller;

import com.internship.tool.entity.ExampleEntity;
import com.internship.tool.exception.EntityNotFoundException;
import com.internship.tool.service.ExampleEntityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/example-entity")
public class ExampleEntityController {
    private final ExampleEntityService service;

    @Autowired
    public ExampleEntityController(ExampleEntityService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public ResponseEntity<Page<ExampleEntity>> getAllPaginated(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExampleEntity> result = service.getAllPaginated(pageable);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExampleEntity> getById(@PathVariable Long id) {
        try {
            ExampleEntity entity = service.getById(id);
            return ResponseEntity.ok(entity);
        } catch (EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ExampleEntity> create(@Valid @RequestBody ExampleEntity entity) {
        ExampleEntity created = service.create(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
