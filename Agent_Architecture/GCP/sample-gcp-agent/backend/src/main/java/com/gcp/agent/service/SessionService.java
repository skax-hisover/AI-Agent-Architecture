package com.gcp.agent.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 세션/메모리 관리 (Firestore / Memorystore 컨셉을 인메모리로 모킹)
 */
@Service
public class SessionService {

    private final Map<String, List<Map<String, String>>> sessions = new ConcurrentHashMap<>();

    public String createSession() {
        String id = UUID.randomUUID().toString();
        sessions.put(id, new ArrayList<>());
        return id;
    }

    public boolean sessionExists(String sessionId) {
        return sessions.containsKey(sessionId);
    }

    public void addToSession(String sessionId, String userMessage, String agentResponse) {
        sessions.computeIfAbsent(sessionId, k -> new ArrayList<>());
        Map<String, String> conv = new HashMap<>();
        conv.put("user", userMessage);
        conv.put("agent", agentResponse);
        conv.put("timestamp", new Date().toString());
        sessions.get(sessionId).add(conv);
    }

    public List<Map<String, String>> getSessionHistory(String sessionId) {
        return sessions.getOrDefault(sessionId, new ArrayList<>());
    }
}

