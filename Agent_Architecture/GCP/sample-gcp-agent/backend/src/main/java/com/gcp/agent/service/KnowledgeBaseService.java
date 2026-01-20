package com.gcp.agent.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * GCP/Vertex AI 관련 지식을 인메모리로 모킹한 간단한 Knowledge Base
 */
@Service
public class KnowledgeBaseService {

    private final Map<String, String> knowledgeBase;

    public KnowledgeBaseService() {
        this.knowledgeBase = new HashMap<>();
        initializeKnowledgeBase();
    }

    private void initializeKnowledgeBase() {
        knowledgeBase.put("gcp", "GCP(Google Cloud Platform)는 Google의 클라우드 플랫폼으로, 다양한 컴퓨팅/스토리지/AI 서비스를 제공합니다.");
        knowledgeBase.put("vertex ai", "Vertex AI는 통합 ML 플랫폼으로, 모델 학습/배포/관리 및 Agent Engine을 제공합니다.");
        knowledgeBase.put("agent engine", "Vertex AI Agent Engine은 에이전트 실행과 멀티 에이전트 패턴을 위한 관리형 런타임입니다.");
        knowledgeBase.put("vertex ai search", "Vertex AI Search는 벡터 검색과 전체 텍스트 검색을 제공하여 RAG를 구현할 수 있습니다.");
        knowledgeBase.put("cloud run", "Cloud Run은 컨테이너 기반 서버리스 실행 환경으로, HTTP 기반 마이크로서비스에 적합합니다.");
        knowledgeBase.put("cloud functions", "Cloud Functions는 이벤트 기반 서버리스 함수 실행 환경입니다.");
        knowledgeBase.put("firestore", "Firestore는 서버리스 NoSQL 데이터베이스로, 세션 및 상태 저장에 적합합니다.");
    }

    public List<String> search(String query) {
        List<String> results = new ArrayList<>();
        String lower = query.toLowerCase();

        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (lower.contains(entry.getKey())) {
                results.add(entry.getValue());
            }
        }
        return results;
    }

    public List<String> getCitations(String query) {
        List<String> citations = new ArrayList<>();
        String lower = query.toLowerCase();

        for (String key : knowledgeBase.keySet()) {
            if (lower.contains(key)) {
                citations.add("Knowledge Base: " + key);
            }
        }
        return citations;
    }
}

