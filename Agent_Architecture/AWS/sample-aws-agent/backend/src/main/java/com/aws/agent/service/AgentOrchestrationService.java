package com.aws.agent.service;

import com.aws.agent.model.AgentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Agent 오케스트레이션 서비스 (Orchestration 레이어)
 * 실제 Bedrock Agent 대신 간단한 로직으로 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentOrchestrationService {
    
    private final KnowledgeBaseService knowledgeBaseService;
    private final ToolService toolService;
    private final SessionService sessionService;
    
    /**
     * 메인 Agent 처리 로직
     */
    public AgentResponse processRequest(String message, String sessionId) {
        log.info("Agent 요청 처리 시작: message={}, sessionId={}", message, sessionId);
        
        // 1. 세션 확인 및 생성
        if (sessionId == null || !sessionService.sessionExists(sessionId)) {
            sessionId = sessionService.createSession();
            log.info("새 세션 생성: {}", sessionId);
        }
        
        // 2. 지식 기반 검색 (RAG)
        List<String> knowledgeResults = knowledgeBaseService.search(message);
        List<String> citations = knowledgeBaseService.getCitations(message);
        
        // 3. 도구 결정 및 실행
        ToolService.Tool selectedTool = toolService.determineTool(message);
        Map<String, Object> toolResult = new HashMap<>();
        String toolUsed = null;
        
        if (selectedTool != ToolService.Tool.NONE) {
            toolUsed = selectedTool.getName();
            log.info("도구 선택: {}", toolUsed);
            
            switch (selectedTool) {
                case CALCULATOR:
                    toolResult = toolService.executeCalculator(message);
                    break;
                case WEATHER:
                    toolResult = toolService.executeWeather(message);
                    break;
                case TIME:
                    toolResult = toolService.executeTime(message);
                    break;
            }
        }
        
        // 4. 응답 생성 (간단한 LLM 모킹)
        String response = generateResponse(message, knowledgeResults, toolResult, selectedTool, sessionId);
        
        // 5. 세션에 대화 저장
        sessionService.addToSession(sessionId, message, response);
        
        // 6. 메타데이터 구성
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("toolUsed", toolUsed);
        metadata.put("knowledgeFound", !knowledgeResults.isEmpty());
        metadata.put("timestamp", new Date().toString());
        
        log.info("Agent 요청 처리 완료: sessionId={}", sessionId);
        
        return AgentResponse.builder()
                .response(response)
                .sessionId(sessionId)
                .citations(citations)
                .metadata(metadata)
                .toolUsed(toolUsed)
                .build();
    }
    
    /**
     * 응답 생성 (실제 Bedrock LLM 대신 간단한 로직)
     */
    private String generateResponse(String message, List<String> knowledgeResults, 
                                   Map<String, Object> toolResult, 
                                   ToolService.Tool tool, 
                                   String sessionId) {
        StringBuilder response = new StringBuilder();
        
        // 도구 결과가 있으면 우선 사용
        if (tool != ToolService.Tool.NONE && !toolResult.isEmpty()) {
            if (toolResult.containsKey("error")) {
                response.append("죄송합니다. ").append(toolResult.get("error"));
            } else {
                switch (tool) {
                    case CALCULATOR:
                        response.append("계산 결과: ")
                                .append(toolResult.get("expression"))
                                .append(" = ")
                                .append(toolResult.get("result"));
                        break;
                    case WEATHER:
                        response.append("현재 ").append(toolResult.get("location"))
                                .append("의 날씨는 ").append(toolResult.get("temperature"))
                                .append(", ").append(toolResult.get("condition"))
                                .append("입니다. (습도: ").append(toolResult.get("humidity")).append(")");
                        break;
                    case TIME:
                        response.append("현재 시간은 ").append(toolResult.get("currentTime"))
                                .append(" (").append(toolResult.get("timezone")).append(") 입니다.");
                        break;
                }
            }
        } else if (!knowledgeResults.isEmpty()) {
            // 지식 기반 결과 사용
            response.append(knowledgeResults.get(0));
            if (knowledgeResults.size() > 1) {
                response.append("\n\n추가 정보: ").append(knowledgeResults.get(1));
            }
        } else {
            // 기본 응답
            response.append(generateDefaultResponse(message));
        }
        
        return response.toString();
    }
    
    /**
     * 기본 응답 생성
     */
    private String generateDefaultResponse(String message) {
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("안녕") || lowerMessage.contains("hello")) {
            return "안녕하세요! AWS AI Agent입니다. 무엇을 도와드릴까요?";
        }
        
        if (lowerMessage.contains("도움") || lowerMessage.contains("help")) {
            return "저는 다음과 같은 기능을 제공할 수 있습니다:\n" +
                   "- 계산기: 수학 연산 (예: '5 + 3 계산해줘')\n" +
                   "- 날씨 조회: 현재 날씨 정보\n" +
                   "- 시간 조회: 현재 시간\n" +
                   "- 지식 검색: AWS, Bedrock, Lambda 등에 대한 정보";
        }
        
        return "이해했습니다. '" + message + "'에 대해 답변드리겠습니다. " +
               "더 구체적인 질문을 해주시면 더 정확한 답변을 드릴 수 있습니다.";
    }
}
