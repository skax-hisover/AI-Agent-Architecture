package com.aws.agent.controller;

import com.aws.agent.model.AgentRequest;
import com.aws.agent.model.AgentResponse;
import com.aws.agent.service.AgentOrchestrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Agent REST API Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AgentController {
    
    private final AgentOrchestrationService agentService;
    
    /**
     * Agent 메시지 처리
     */
    @PostMapping("/chat")
    public ResponseEntity<AgentResponse> chat(@Valid @RequestBody AgentRequest request) {
        log.info("Agent 채팅 요청 수신: {}", request.getMessage());
        
        AgentResponse response = agentService.processRequest(
                request.getMessage(), 
                request.getSessionId()
        );
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "AWS Agent Backend"));
    }
}
