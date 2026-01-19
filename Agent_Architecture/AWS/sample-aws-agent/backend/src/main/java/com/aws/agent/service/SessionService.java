package com.aws.agent.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 세션 관리 서비스 (Memory/Session State 역할)
 */
@Service
public class SessionService {
    
    private final Map<String, List<Map<String, String>>> sessions;
    
    public SessionService() {
        this.sessions = new ConcurrentHashMap<>();
    }
    
    /**
     * 세션 ID 생성
     */
    public String createSession() {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, new ArrayList<>());
        return sessionId;
    }
    
    /**
     * 세션 존재 여부 확인
     */
    public boolean sessionExists(String sessionId) {
        return sessions.containsKey(sessionId);
    }
    
    /**
     * 세션에 대화 추가
     */
    public void addToSession(String sessionId, String userMessage, String agentResponse) {
        if (!sessions.containsKey(sessionId)) {
            sessions.put(sessionId, new ArrayList<>());
        }
        
        Map<String, String> conversation = new HashMap<>();
        conversation.put("user", userMessage);
        conversation.put("agent", agentResponse);
        conversation.put("timestamp", new Date().toString());
        
        sessions.get(sessionId).add(conversation);
    }
    
    /**
     * 세션 히스토리 조회
     */
    public List<Map<String, String>> getSessionHistory(String sessionId) {
        return sessions.getOrDefault(sessionId, new ArrayList<>());
    }
    
    /**
     * 세션 컨텍스트 가져오기 (최근 N개 대화)
     */
    public String getSessionContext(String sessionId, int recentCount) {
        List<Map<String, String>> history = getSessionHistory(sessionId);
        if (history.isEmpty()) {
            return "";
        }
        
        StringBuilder context = new StringBuilder();
        int start = Math.max(0, history.size() - recentCount);
        
        for (int i = start; i < history.size(); i++) {
            Map<String, String> conv = history.get(i);
            context.append("사용자: ").append(conv.get("user")).append("\n");
            context.append("에이전트: ").append(conv.get("agent")).append("\n\n");
        }
        
        return context.toString();
    }
}
