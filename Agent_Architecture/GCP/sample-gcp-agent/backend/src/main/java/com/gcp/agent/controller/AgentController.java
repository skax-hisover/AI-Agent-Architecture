package com.gcp.agent.controller;

import com.gcp.agent.model.AgentRequest;
import com.gcp.agent.model.AgentResponse;
import com.gcp.agent.service.AgentOrchestrationService;
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
@CrossOrigin(origins = "http://localhost:3002")
public class AgentController {

    private final AgentOrchestrationService agentService;

    @PostMapping("/chat")
    public ResponseEntity<AgentResponse> chat(@Valid @RequestBody AgentRequest request) {
        log.info("GCP Agent 채팅 요청: {}", request.getMessage());
        AgentResponse response = agentService.processRequest(request.getMessage(), request.getSessionId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "GCP Agent Backend"));
    }
}

