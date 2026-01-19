package com.aws.agent.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 간단한 Knowledge Base 서비스 (실제 Bedrock Knowledge Base 대신 인메모리 구현)
 */
@Service
public class KnowledgeBaseService {
    
    private final Map<String, String> knowledgeBase;
    
    public KnowledgeBaseService() {
        this.knowledgeBase = new HashMap<>();
        initializeKnowledgeBase();
    }
    
    private void initializeKnowledgeBase() {
        knowledgeBase.put("aws", "AWS는 Amazon Web Services의 약자로, 클라우드 컴퓨팅 서비스를 제공합니다.");
        knowledgeBase.put("bedrock", "Amazon Bedrock은 AWS의 완전 관리형 생성형 AI 서비스입니다.");
        knowledgeBase.put("lambda", "AWS Lambda는 서버리스 컴퓨팅 서비스로, 코드를 실행할 수 있습니다.");
        knowledgeBase.put("agent", "AI Agent는 자율적인 의사결정과 행동 실행이 가능한 시스템입니다.");
        knowledgeBase.put("rag", "RAG는 Retrieval-Augmented Generation의 약자로, 지식 기반을 활용한 생성형 AI 접근법입니다.");
    }
    
    /**
     * 지식 베이스에서 관련 정보 검색
     */
    public List<String> search(String query) {
        List<String> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (Map.Entry<String, String> entry : knowledgeBase.entrySet()) {
            if (lowerQuery.contains(entry.getKey())) {
                results.add(entry.getValue());
            }
        }
        
        return results;
    }
    
    /**
     * 인용 정보 생성
     */
    public List<String> getCitations(String query) {
        List<String> citations = new ArrayList<>();
        String lowerQuery = query.toLowerCase();
        
        for (String key : knowledgeBase.keySet()) {
            if (lowerQuery.contains(key)) {
                citations.add("Knowledge Base: " + key);
            }
        }
        
        return citations;
    }
}
