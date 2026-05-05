package com.internship.tool.controller;

import com.internship.tool.entity.ExampleEntity;
import com.internship.tool.service.ExampleEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache-test")
public class CacheTimingController {
    @Autowired
    private ExampleEntityService service;

    @GetMapping("/by-id/{id}")
    public ResponseEntity<?> testCacheById(@PathVariable Long id) {
        long start = System.nanoTime();
        ExampleEntity entity = service.getById(id);
        long duration = System.nanoTime() - start;
        return ResponseEntity.ok("Time (ns): " + duration + ", Entity: " + (entity != null ? entity.getName() : "null"));
    }
}
