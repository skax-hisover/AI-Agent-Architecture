package com.azure.agent.controller;

import com.azure.agent.model.AgentRequest;
import com.azure.agent.model.AgentResponse;
import com.azure.agent.service.AgentOrchestrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3001")
public class AgentController {

    private final AgentOrchestrationService agentService;

    @PostMapping("/chat")
    public ResponseEntity<AgentResponse> chat(@Valid @RequestBody AgentRequest request) {
        log.info("Azure Agent 채팅 요청: {}", request.getMessage());
        AgentResponse response = agentService.processRequest(request.getMessage(), request.getSessionId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "Azure Agent Backend"));
    }
}

