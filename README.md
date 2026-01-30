# AI Agent Architecture Best Practices

이 저장소는 주요 클라우드 서비스 제공자(CSP)인 **AWS**, **Azure**, **GCP**의 AI Agent Architecture Best Practices를 정리한 문서 모음입니다.

## 📋 개요

AI Agent Architecture는 단순한 대화형 챗봇을 넘어서, **자율적인 의사결정과 행동 실행이 가능한 에이전트 시스템**을 구축하기 위한 아키텍처입니다. 각 클라우드 제공자는 고유한 서비스와 접근 방식을 제공하며, 이 문서들은 각 플랫폼에서 AI Agent를 설계, 구축, 운영할 때 따라야 할 모범 사례를 정리합니다.

### 공통 핵심 요소

모든 CSP의 AI Agent Architecture는 다음 핵심 요소를 포함합니다:

- **Foundation Models**: 에이전트의 추론 및 계획 수립을 위한 LLM
- **Orchestration**: 다단계 작업 조정 및 워크플로우 제어
- **Tools / Actions**: 외부 API 호출, 데이터베이스 접근 등 도구 통합
- **Knowledge Base**: RAG(Retrieval-Augmented Generation)를 통한 지식 기반
- **Memory Management**: 세션 상태 및 장기 메모리 관리
- **Security & Governance**: 인증/권한, 암호화, 콘텐츠 안전성
- **Observability**: 로깅, 추적, 모니터링

---

## ☁️ 클라우드별 AI Agent Architecture

### AWS

**Amazon Bedrock**과 **SageMaker**를 중심으로 한 에이전트 시스템으로, **자동화, 추론, 지식 근거(grounding), 보안, 관찰성**을 핵심으로 합니다.

#### 주요 특징
- **Amazon Bedrock Agents**: 완전 관리형 에이전트 오케스트레이션
- **Bedrock Knowledge Bases**: RAG를 위한 지식 베이스
- **Bedrock Guardrails**: 콘텐츠 필터링 및 정책 적용
- **Lambda & Step Functions**: 서버리스 워크플로우 오케스트레이션
- **Grounded Agent Workflow**: 실제 도메인 지식 기반 응답 생성
- **동기/비동기 통신 패턴**: InvokeAgent API, EventBridge, Step Functions 기반 통신
- **공유 DB + Vector Store**: PostgreSQL + pgvector, Bedrock Knowledge Base + OpenSearch
- **LLMOps 파이프라인**: Bedrock Agent 버전 관리, 평가, CI/CD 통합
- **End-to-End Tracing**: AWS Distro for OpenTelemetry (ADOT) + X-Ray 기반 추적

#### 주요 서비스
- Amazon Bedrock, Amazon Bedrock Agents
- Amazon Bedrock Knowledge Bases
- AWS Lambda, AWS Step Functions, Amazon EventBridge
- Amazon RDS (PostgreSQL + pgvector), Amazon DynamoDB, Amazon S3, Amazon OpenSearch
- Amazon CloudWatch, AWS X-Ray, AWS Distro for OpenTelemetry (ADOT)

📖 **[AWS AI Agent Architecture Best Practices 상세 문서 보기](./Agent_Architecture/AWS/AWS_AI_Agent_Architecture_Best_Practices.md)**

---

### Azure

**Azure OpenAI Service**와 **Azure AI Studio**를 중심으로 한 에이전트 시스템으로, **함수 호출(Function Calling)**, **도구 통합**, **RAG**, **보안**, **관찰성**을 핵심으로 합니다.

#### 주요 특징
- **Azure OpenAI Assistants API**: 대화형 에이전트 구축을 위한 관리형 서비스
- **함수 호출(Function Calling)**: 외부 시스템 통합을 위한 표준화된 방식
- **Azure AI Search**: 벡터 검색 및 RAG 구현
- **Azure Functions**: 서버리스 함수를 통한 도구 실행
- **Azure AI Content Safety**: 콘텐츠 필터링 및 안전성 검사
- **동기/비동기 통신 패턴**: Assistants API (Run Polling), Chat Completions API, Service Bus, Logic Apps
- **공유 DB + Vector Store**: Azure PostgreSQL + pgvector, Azure AI Search + Cosmos DB
- **LLMOps 파이프라인**: Azure AI Studio + Prompt Flow, Azure ML MLOps
- **End-to-End Tracing**: OpenTelemetry SDK + Application Insights 기반 추적

#### 주요 서비스
- Azure OpenAI Service, Azure AI Studio, Azure AI Assistants API
- Azure AI Search
- Azure Functions, Azure Logic Apps, Azure Service Bus
- Azure Database for PostgreSQL (pgvector), Azure Cosmos DB, Azure Blob Storage
- Azure Monitor, Application Insights, Log Analytics, OpenTelemetry SDK

📖 **[Azure AI Agent Architecture Best Practices 상세 문서 보기](./Agent_Architecture/Azure/Azure_AI_Agent_Architecture_Best_Practices.md)**

---

### GCP

**Vertex AI Agent Engine**과 **Agent Development Kit (ADK)**를 중심으로 한 에이전트 시스템으로, **멀티 에이전트 협업**, **표준화된 프로토콜 (A2A, MCP)**, **메모리 관리**, **보안**, **관찰성**을 핵심으로 합니다.

#### 주요 특징
- **Vertex AI Agent Engine**: 완전 관리형 에이전트 런타임 환경
- **Agent-to-Agent (A2A) 프로토콜**: 에이전트 간 표준화된 통신
- **Model Context Protocol (MCP)**: 도구 및 외부 시스템과의 표준화된 통합
- **Memory Bank**: 단기 및 장기 메모리 관리
- **다양한 디자인 패턴**: Sequential, Parallel, Loop, Coordinator, Hierarchical 등
- **동기/비동기 통신 패턴**: Agent Engine 기본 통신, Pub/Sub, Cloud Workflows, A2A 프로토콜
- **공유 DB + Vector Store**: AlloyDB/Cloud SQL + pgvector, Vertex AI Search + Firestore
- **LLMOps 파이프라인**: Agentverse 프레임워크, Agent 버전 관리, 평가, 배포 자동화
- **End-to-End Tracing**: OpenTelemetry SDK + Cloud Trace 기반 추적

#### 주요 서비스
- Vertex AI, Vertex AI Agent Engine, Agent Development Kit (ADK)
- Vertex AI Search
- Cloud Run, Cloud Functions, GKE, Cloud Workflows, Pub/Sub
- AlloyDB (pgvector), Cloud SQL (pgvector), Firestore, BigQuery, Memorystore
- Cloud Logging, Cloud Monitoring, Cloud Trace, OpenTelemetry SDK

📖 **[GCP AI Agent Architecture Best Practices 상세 문서 보기](./Agent_Architecture/GCP/GCP_AI_Agent_Architecture_Best_Practices.md)**

---

## 📚 문서 구조

각 CSP별 Best Practices 문서는 다음 구조로 작성되었습니다:

1. **개요**: 각 CSP의 AI Agent Architecture 특징 및 핵심 개념
2. **핵심 아키텍처 구성요소**: 레이어 구조 및 주요 서비스 소개
3. **아키텍처 설계 원칙**: 핵심 설계 원칙 및 가이드라인
4. **Best Practices 상세**: 실전 가이드 및 구현 방법
5. **Application ↔ Agent 통신 패턴**: 동기/비동기 호출 패턴 및 선택 기준
6. **Application ↔ Agent 공유 DB + Vector Store 구축**: 공유 데이터베이스 및 벡터 스토어 아키텍처 패턴
7. **보안 및 거버넌스**: 보안, 규정 준수, 책임 있는 AI
8. **운영 및 모니터링**: 로깅, 모니터링, 테스트 전략, End-to-End Tracing
9. **LLMOps / AgentOps 파이프라인**: 개발, 평가, 배포, 모니터링 파이프라인
10. **참고 아키텍처 패턴**: 주요 아키텍처 패턴 예시

---

## 🔍 주요 Best Practices 요약

### 공통 Best Practices

모든 CSP에서 공통적으로 권장되는 Best Practices:

1. **Agent 스코프 명확화**: 각 에이전트의 역할, 도메인, 권한을 명확히 정의
2. **모듈화 및 분리**: 작고 집중된 마이크로 에이전트 구조 설계
3. **RAG + 지식 기반 통합**: 엔터프라이즈 데이터를 활용한 정확한 응답 생성
4. **Application ↔ Agent 통신 패턴 설계**: 동기/비동기 호출 기준 수립 및 통신 패턴 선택
5. **공유 데이터베이스 및 벡터 스토어 구축**: Application과 Agent 간 데이터 일관성 및 효율성 확보
6. **보안 중심 설계**: 최소 권한 원칙, 암호화, Guardrails 적용
7. **관찰성 우선 설계**: 전체 워크플로우 추적, 로깅, 모니터링, End-to-End Tracing
8. **LLMOps / AgentOps 파이프라인**: 개발, 평가, 버전 관리, 배포, 모니터링 자동화
9. **점진적 확장 전략**: Crawl-Walk-Run 방식으로 단계적 확장
10. **안정성 및 복원력**: Fault Isolation, Fallback 메커니즘, 재시도 전략

### CSP별 차별화된 특징

| CSP | 주요 차별화 포인트 |
|-----|-------------------|
| **AWS** | Bedrock Guardrails, Grounded Agent Workflow, 서버리스 중심 |
| **Azure** | Assistants API, Azure AI Content Safety, 엔터프라이즈 통합 |
| **GCP** | A2A/MCP 프로토콜, 다양한 멀티 에이전트 패턴, Agent Engine |

---

## 🚀 시작하기

각 CSP의 AI Agent Architecture를 구축하기 시작하려면:

1. 해당 CSP의 Best Practices 문서를 참고하세요
2. 프로젝트 요구사항에 맞는 아키텍처 패턴을 선택하세요
3. **Application ↔ Agent 통신 패턴**을 설계하세요 (동기/비동기 호출 기준 수립)
4. **공유 데이터베이스 및 벡터 스토어** 아키텍처를 설계하세요
5. 보안 및 거버넌스 요구사항을 확인하세요
6. **LLMOps / AgentOps 파이프라인**을 설계하세요 (개발, 평가, 배포, 모니터링)
7. **End-to-End Tracing** 구조를 설계하세요 (OpenTelemetry 기반)
8. 점진적 확장 전략(Crawl-Walk-Run)을 수립하세요

---

## 📖 상세 문서

- [AWS AI Agent Architecture Best Practices](./Agent_Architecture/AWS/AWS_AI_Agent_Architecture_Best_Practices.md)
- [Azure AI Agent Architecture Best Practices](./Agent_Architecture/Azure/Azure_AI_Agent_Architecture_Best_Practices.md)
- [GCP AI Agent Architecture Best Practices](./Agent_Architecture/GCP/GCP_AI_Agent_Architecture_Best_Practices.md)


---

## 🤝 기여

이 문서는 각 CSP의 공식 문서 및 Best Practices를 기반으로 작성되었습니다. 개선 사항이나 추가 정보가 있다면 기여해주세요.

---

**참고**: 이 문서들은 각 클라우드 제공자의 공식 문서와 Best Practices 가이드를 참고하여 작성되었으며, 실제 구현 시 최신 공식 문서를 확인하는 것을 권장합니다.
