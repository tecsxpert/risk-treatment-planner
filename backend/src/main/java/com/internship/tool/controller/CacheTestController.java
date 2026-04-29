package com.internship.tool.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cache-test")
public class CacheTestController {
    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/status")
    public ResponseEntity<?> cacheStatus() {
        return ResponseEntity.ok(cacheManager.getCacheNames());
    }
}
