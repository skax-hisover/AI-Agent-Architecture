package com.azure.agent.service;

import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Azure AI Search 기반 RAG를 단순 모킹한 인메모리 Knowledge Base
 */
@Service
public class KnowledgeBaseService {

    private final Map<String, String> knowledgeBase;

    public KnowledgeBaseService() {
        this.knowledgeBase = new HashMap<>();
        initializeKnowledgeBase();
    }

    private void initializeKnowledgeBase() {
        knowledgeBase.put("azure", "Azure는 Microsoft의 클라우드 플랫폼으로, 다양한 IaaS/PaaS/SaaS 서비스를 제공합니다.");
        knowledgeBase.put("azure openai", "Azure OpenAI Service는 GPT 계열 모델을 Azure 상에서 안전하게 사용할 수 있게 해주는 서비스입니다.");
        knowledgeBase.put("azure ai search", "Azure AI Search는 벡터 검색과 전체 텍스트 검색을 제공하는 검색 서비스입니다.");
        knowledgeBase.put("functions", "Azure Functions는 서버리스 함수 실행 환경으로, HTTP 트리거 등을 이용해 코드를 실행할 수 있습니다.");
        knowledgeBase.put("logic apps", "Azure Logic Apps는 워크플로우 오케스트레이션을 위한 서버리스 서비스입니다.");
        knowledgeBase.put("rag", "RAG는 Retrieval-Augmented Generation으로, 검색된 지식을 프롬프트에 합쳐 더 정확한 응답을 생성하는 패턴입니다.");
    }

    /**
     * 간단한 키워드 매칭 기반 검색
     */
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

    /**
     * 인용 정보 생성
     */
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

