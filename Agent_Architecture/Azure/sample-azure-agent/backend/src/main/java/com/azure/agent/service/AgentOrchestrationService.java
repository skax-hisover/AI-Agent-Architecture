package com.azure.agent.service;

import com.azure.agent.model.AgentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Azure OpenAI Assistants + Azure Functions + Azure AI Search 패턴을 모킹한 오케스트레이션 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AgentOrchestrationService {

    private final KnowledgeBaseService knowledgeBaseService;
    private final ToolService toolService;
    private final SessionService sessionService;

    public AgentResponse processRequest(String message, String sessionId) {
        log.info("Azure Agent 요청 처리 시작: message={}, sessionId={}", message, sessionId);

        if (sessionId == null || !sessionService.sessionExists(sessionId)) {
            sessionId = sessionService.createSession();
            log.info("새 세션 생성: {}", sessionId);
        }

        // 1) RAG (Azure AI Search 개념 모킹)
        List<String> kbResults = knowledgeBaseService.search(message);
        List<String> citations = knowledgeBaseService.getCitations(message);

        // 2) 함수 호출(Function Calling) 모킹
        ToolService.Tool tool = toolService.determineTool(message);
        Map<String, Object> toolResult = new HashMap<>();
        String toolUsed = null;
        if (tool != ToolService.Tool.NONE) {
            toolUsed = tool.getName();
            switch (tool) {
                case CALCULATOR -> toolResult = toolService.executeCalculator(message);
                case WEATHER -> toolResult = toolService.executeWeather(message);
                case TIME -> toolResult = toolService.executeTime(message);
            }
        }

        // 3) LLM 응답 생성 모킹
        String responseText = generateResponse(message, kbResults, toolResult, tool);

        // 4) 세션 저장
        sessionService.addToSession(sessionId, message, responseText);

        // 5) 메타데이터
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("toolUsed", toolUsed);
        metadata.put("knowledgeFound", !kbResults.isEmpty());
        metadata.put("timestamp", new Date().toString());
        metadata.put("platform", "Azure (mock)");

        return AgentResponse.builder()
                .response(responseText)
                .sessionId(sessionId)
                .citations(citations)
                .metadata(metadata)
                .toolUsed(toolUsed)
                .build();
    }

    private String generateResponse(String message,
                                    List<String> kbResults,
                                    Map<String, Object> toolResult,
                                    ToolService.Tool tool) {
        StringBuilder sb = new StringBuilder();

        if (tool != ToolService.Tool.NONE && !toolResult.isEmpty()) {
            if (toolResult.containsKey("error")) {
                sb.append("죄송합니다. ").append(toolResult.get("error"));
            } else {
                switch (tool) {
                    case CALCULATOR -> sb.append("계산 결과: ")
                            .append(toolResult.get("expression"))
                            .append(" = ")
                            .append(toolResult.get("result"));
                    case WEATHER -> sb.append("현재 ")
                            .append(toolResult.get("location"))
                            .append("의 날씨는 ")
                            .append(toolResult.get("temperature"))
                            .append(", ")
                            .append(toolResult.get("condition"))
                            .append(" 입니다.");
                    case TIME -> sb.append("현재 시간은 ")
                            .append(toolResult.get("currentTime"))
                            .append(" (")
                            .append(toolResult.get("timezone"))
                            .append(") 입니다.");
                }
            }
        } else if (!kbResults.isEmpty()) {
            sb.append(kbResults.get(0));
        } else {
            String lower = message.toLowerCase();
            if (lower.contains("안녕") || lower.contains("hello")) {
                return "안녕하세요! Azure AI Agent입니다. 무엇을 도와드릴까요?";
            }
            if (lower.contains("도움") || lower.contains("help")) {
                return """
                        저는 다음과 같은 기능을 제공할 수 있습니다:
                        - 계산기: 예) "5 + 3 계산해줘"
                        - 날씨 조회(모킹)
                        - 시간 조회
                        - Azure / Azure OpenAI / Azure AI Search 기본 설명
                        """;
            }
            sb.append("Azure 기반 에이전트로서 '")
                    .append(message)
                    .append("'에 대해 답변을 시도합니다. 더 구체적인 질문을 주시면 좋습니다.");
        }

        return sb.toString();
    }
}

