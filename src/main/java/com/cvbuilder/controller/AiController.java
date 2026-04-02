package com.cvbuilder.controller;

import com.cvbuilder.service.ClaudeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiController {

    private final ClaudeService service;

    public AiController(ClaudeService service) {
        this.service = service;
    }

    @PostMapping("/analyze")
    public ResponseEntity<?> analyze(@RequestBody Map<String, String> request) {
        String text = request.get("text");

        String result = service.analyzeCVSection(text);

        return ResponseEntity.ok(result);
    }
}