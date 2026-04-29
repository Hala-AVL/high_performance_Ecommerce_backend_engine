package com.EcommerceApp.H2NS.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WelcomeController {

    @GetMapping("/")
    public Map<String, Object> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "🚀 E-Commerce High-Performance Backend Engine");
        response.put("status", "RUNNING");
        response.put("version", "1.0.0");
        response.put("project", "High-Performance E-Commerce Backend Engine");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    @GetMapping("/api/status")
    public Map<String, String> status() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "E-Commerce Engine");
        status.put("environment", "development");
        return status;
    }
}