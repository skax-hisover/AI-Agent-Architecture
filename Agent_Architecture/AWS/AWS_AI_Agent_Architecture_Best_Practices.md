# AWS AI Agent Architecture Best Practices

## 목차
1. [개요](#개요)
2. [핵심 아키텍처 구성요소](#핵심-아키텍처-구성요소)
3. [아키텍처 설계 원칙](#아키텍처-설계-원칙)
4. [Best Practices 상세](#best-practices-상세)
5. [보안 및 거버넌스](#보안-및-거버넌스)
6. [운영 및 모니터링](#운영-및-모니터링)
7. [아키텍처 구성도](#아키텍처-구성도)
8. [Application ↔ Agent 통신 패턴](#application--agent-통신-패턴)
9. [참고 아키텍처 패턴](#참고-아키텍처-패턴)
10. [체크리스트](#체크리스트)
11. [참고 자료](#참고-자료)

---

## 개요

AWS의 AI Agent Architecture는 단순한 대화형 챗봇이 아닌, **자율적인 의사결정과 행동 실행이 가능한 에이전트 시스템**을 구축하기 위한 아키텍처입니다. 핵심은 **자동화, 추론, 지식 근거(grounding), 보안, 관찰성(observability)**을 확보하는 것입니다.

### 주요 특징
- **도구 호출(Tool Invocation)**: 외부 API, 데이터베이스, 서비스와의 통합
- **지식 기반 활용**: RAG(Retrieval-Augmented Generation)를 통한 엔터프라이즈 데이터 활용
- **실행 가능한 워크플로우**: 단순 응답 생성이 아닌 실제 작업 수행
- **오류 복원(Resilience)**: 장애 대응 및 복구 메커니즘
- **거버넌스(Governance)**: 보안, 규정 준수, 감사 추적

---

## 핵심 아키텍처 구성요소

### 1. 레이어 구조

| 레이어 | 역할 | 주요 AWS 서비스 |
|--------|------|----------------|
| **Foundation Models** | Agent의 추론 및 계획 수립 | Amazon Bedrock, SageMaker |
| **Orchestration** | 다단계 작업 조정, 도구 선택, 워크플로우 제어 | Lambda, Step Functions, EventBridge |
| **Tools / Action Groups** | 외부 API 호출, 계산, DB 접근 | Lambda, API Gateway, OpenAPI |
| **Knowledge Base** | 문서, 데이터베이스, 벡터 스토리지 | Bedrock Knowledge Bases, OpenSearch, S3 |
| **Memory / Session State** | 장기 메모리, 세션 상태, 사용자 히스토리 | DynamoDB, S3, Vector DB |
| **Security & Governance** | 인증/권한, 암호화, Guardrails | IAM, KMS, Bedrock Guardrails |
| **Observability** | 로그, 추적, 모니터링 | CloudWatch, X-Ray, OpenTelemetry |

### 2. 주요 AWS 서비스

#### AI/ML 서비스
- **Amazon Bedrock**: Foundation Models 관리 및 Agent 런타임
- **Amazon Bedrock Agents**: 에이전트 오케스트레이션
- **Amazon Bedrock Knowledge Bases**: RAG를 위한 지식 베이스
- **Amazon Bedrock Guardrails**: 콘텐츠 필터링 및 정책 적용
- **Amazon SageMaker**: 커스텀 모델 훈련 및 배포

#### 오케스트레이션 및 컴퓨팅
- **AWS Lambda**: 서버리스 함수 실행
- **AWS Step Functions**: 복잡한 워크플로우 오케스트레이션
- **Amazon EventBridge**: 이벤트 기반 아키텍처

#### 스토리지 및 데이터베이스
- **Amazon S3**: 문서 및 객체 저장
- **Amazon DynamoDB**: 세션 상태 및 메타데이터 저장
- **Amazon OpenSearch**: 벡터 검색 및 지식 검색
- **Amazon RDS / Aurora**: 관계형 데이터 저장

#### 보안 및 거버넌스
- **AWS IAM**: 접근 제어 및 권한 관리
- **AWS KMS**: 암호화 키 관리
- **AWS Secrets Manager**: 시크릿 관리

#### 관찰성
- **Amazon CloudWatch**: 로깅 및 모니터링
- **AWS X-Ray**: 분산 추적
- **OpenTelemetry**: 표준화된 관찰성

---

## 아키텍처 설계 원칙

### 1. Agent 스코프 명확화 (Scoping)
- **원칙**: 각 에이전트의 역할, 도메인, 사용 가능한 도구, 권한을 명확히 정의
- **이유**: 복잡성 감소, 보안 위험 감소, 유지보수성 향상
- **실행**:
  - 단일 "슈퍼 에이전트" 대신 역할별로 분리된 마이크로 에이전트 구조
  - 각 에이전트의 책임 범위 문서화
  - 도메인별 전문화된 에이전트 설계

### 2. 모듈화 및 분리 (Modularity & Separation)
- **원칙**: 작고 집중된 마이크로 에이전트 구조
- **이유**: 확장성, 유지보수성, 장애 격리
- **실행**:
  - 역할별 에이전트 분리 (예: 검색 에이전트, 분석 에이전트, 실행 에이전트)
  - 에이전트 간 명확한 인터페이스 정의
  - 독립적인 배포 및 스케일링 가능

### 3. 결정론적 로직 vs LLM 활용 균형
- **원칙**: 가능하면 결정론적 로직 사용, LLM은 추론과 복잡한 의도 해석에만 활용
- **이유**: 비용 절감, 지연 시간 감소, 예측 가능성 향상
- **실행**:
  - 데이터 유효성 검증, 형식 검증은 코드로 처리
  - LLM은 추론, 의도 해석, 도구 선택에 사용
  - 결과에 대한 검증(guardrails, validation) 필수

### 4. 안정성 및 복원력 (Resilience)
- **원칙**: Fault Isolation, Fallback 메커니즘, 재시도 전략
- **이유**: 단일 장애점 제거, 시스템 안정성 확보
- **실행**:
  - 구성 요소 간 경계 분리
  - 타임아웃 설정 및 Circuit Breaker 패턴
  - Fallback 경로 및 Human Escalation 메커니즘
  - 부하 예측 및 스케일링 준비

### 5. 관찰성 우선 설계 (Observability First)
- **원칙**: 전체 워크플로우 추적, 로깅, 모니터링
- **이유**: 디버깅, 성능 최적화, 문제 해결
- **실행**:
  - Trace ID를 통한 전체 요청 추적
  - 프롬프트, 응답, 도구 호출 로깅
  - 실시간 모니터링 및 알림 설정

---

## Best Practices 상세

### 1. RAG + 지식 기반 통합

#### 권장 사항
- **Knowledge Base 구축**: 엔터프라이즈 데이터를 벡터화하여 검색 가능하게 구성
- **인용(Citation) 제공**: 응답에 출처 정보 포함하여 신뢰성 확보
- **지식 최신성 유지**: 정기적인 데이터 동기화 및 업데이트

#### 구현 방법
```python
# 예시: Bedrock Knowledge Base 활용
- Amazon Bedrock Knowledge Bases 설정
- 데이터 소스 연결 (S3, RDS 등)
- 벡터 임베딩 및 인덱싱
- 검색 결과를 프롬프트에 포함
- 인용 정보를 응답에 첨부
```

### 2. 프롬프트 및 Action 정의 관리

#### 권장 사항
- **명확한 Schema 정의**: 도구(Action)의 입력/출력 스키마 명확히 정의
- **버전 관리**: 프롬프트 템플릿, Action 정의를 버전 관리
- **테스트 파이프라인**: 자동화된 테스트 및 평가 파이프라인 구축

#### 구현 방법
- OpenAPI 스펙을 활용한 Action 정의
- Git을 통한 프롬프트 템플릿 버전 관리
- CI/CD 파이프라인에 테스트 단계 포함
- A/B 테스트를 통한 프롬프트 최적화

### 3. 보안 중심 설계 (Security by Design)

#### IAM 최소 권한 원칙
- 각 에이전트, Action Group에 최소한의 권한만 부여
- 역할 기반 접근 제어(RBAC) 구현
- 정기적인 권한 검토 및 정리

#### 데이터 보호
- **암호화**: KMS를 사용한 데이터 암호화 (저장 시, 전송 시)
- **Customer Managed Keys**: 고객 관리형 키 사용
- **PII 처리**: 민감 정보 탈식별화 및 필터링

#### 입력/출력 검증
- **Guardrails**: Bedrock Guardrails를 통한 콘텐츠 필터링
- **Prompt Injection 방지**: 사용자 입력 검증 및 필터링
- **출력 검증**: 생성된 응답의 유효성 검사

### 4. 멀티 에이전트 협업 패턴

#### 워크플로우 에이전트 vs 협업형 에이전트
- **워크플로우 에이전트**: 순차적, 규칙 기반 작업에 적합
- **협업형 에이전트**: 복잡한 작업, 동적 의사결정에 적합

#### 구현 예시
```
Coordinator Agent
  ├── Search Agent (지식 검색)
  ├── Analysis Agent (데이터 분석)
  ├── Execution Agent (작업 실행)
  └── Validation Agent (결과 검증)
```

### 5. 점진적 확장 전략 (Crawl-Walk-Run)

#### 단계별 접근
1. **Crawl**: 내부 사용자 대상, 제한된 기능
2. **Walk**: 확장된 사용자 그룹, 추가 기능
3. **Run**: 전체 사용자 대상, 멀티 에이전트 협업

#### 이점
- 리스크 감소
- 점진적 학습 및 개선
- 안정성 확보

### 6. 비용 및 성능 최적화

#### 모델 선택 전략
- **경량 모델**: 단순 쿼리, 빠른 응답 필요 시
- **고성능 모델**: 복잡한 분석, 정확도 중요 시
- **하이브리드 접근**: 작업 유형에 따라 모델 선택

#### 최적화 기법
- **프롬프트 캐싱**: 반복적인 프롬프트 캐싱
- **병렬 처리**: 독립적인 작업 병렬 실행
- **비동기 처리**: 장시간 작업은 비동기로 처리
- **함수 호출 최소화**: 불필요한 도구 호출 감소

### 7. Application ↔ Agent 공유 DB + Vector Store 구축

#### 아키텍처 패턴

**패턴 1: PostgreSQL + pgvector 통합 패턴**
- **구성**: RDS PostgreSQL + pgvector Extension
- **특징**: 
  - Application 데이터와 벡터 임베딩을 동일 DB에 저장
  - 관계형 데이터와 벡터 검색을 한 곳에서 처리
  - 트랜잭션 일관성 보장
- **적합한 경우**: 
  - 소규모~중규모 데이터
  - 관계형 데이터와 벡터 데이터가 밀접하게 연관
  - 운영 단순화가 우선
- **구현 방법**:
  - RDS PostgreSQL 인스턴스 생성
  - pgvector extension 활성화
  - 벡터 컬럼 추가 및 인덱스 생성
  - Application과 Agent가 동일 DB 접근

**패턴 2: Bedrock Knowledge Base + OpenSearch 패턴**
- **구성**: Bedrock Knowledge Bases + OpenSearch Serverless
- **특징**:
  - 관리형 Vector Store 제공
  - S3에 원본 문서 저장, OpenSearch에 벡터 인덱스
  - Application과 Agent가 동일 Knowledge Base 공유
- **적합한 경우**:
  - 대규모 문서 기반 RAG
  - 관리형 서비스 선호
  - 자동 인덱싱 및 동기화 필요
- **구현 방법**:
  - Bedrock Knowledge Base 생성
  - S3 데이터 소스 연결
  - OpenSearch Serverless를 Vector Store로 설정
  - Application과 Agent가 Knowledge Base API 공유

**패턴 3: 하이브리드 패턴 (RDS + OpenSearch + DynamoDB)**
- **구성**: 
  - RDS: Application 관계형 데이터
  - OpenSearch: Vector Store
  - DynamoDB: 세션 상태 및 메타데이터
- **특징**:
  - 각 구성 요소별 최적화
  - 독립적 스케일링
  - 데이터 동기화 필요
- **적합한 경우**:
  - 대규모 데이터
  - 고성능 벡터 검색 필요
  - 각 구성 요소별 독립적 관리 필요

#### 데이터 동기화 전략
- **CDC (Change Data Capture)**: DMS를 통한 실시간 동기화
- **이벤트 기반**: EventBridge를 통한 비동기 동기화
- **배치 동기화**: 정기적인 배치 작업으로 데이터 동기화

#### 접근 제어 및 보안
- **IAM 역할 분리**: Application과 Agent별 IAM 역할 분리
- **VPC 엔드포인트**: 프라이빗 네트워크 연결
- **암호화**: KMS를 통한 데이터 암호화 (저장 시, 전송 시)

#### 참고 자료
- [AWS RDS PostgreSQL Extensions - pgvector](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/Appendix.PostgreSQL.CommonDBATasks.Extensions.html#Appendix.PostgreSQL.CommonDBATasks.Extensions.pgvector)
- [Amazon Bedrock Knowledge Bases](https://docs.aws.amazon.com/bedrock/latest/userguide/knowledge-base.html)
- [Amazon OpenSearch Serverless](https://docs.aws.amazon.com/opensearch-service/latest/developerguide/serverless.html)

---

## 보안 및 거버넌스

### 1. Guardrails 구현

#### Amazon Bedrock Guardrails 활용
- **콘텐츠 필터링**: 불법, 증오 발언, 민감 주제 필터링
- **정책 적용**: 조직 수준의 정책 자동 적용
- **사용자 정의 필터**: 도메인별 커스텀 필터 설정

### 2. 프롬프트 템플릿 관리

#### 보안 조치
- **접근 제어**: 누가 프롬프트를 변경할 수 있는지 제한
- **리뷰 절차**: 변경 사항에 대한 승인 프로세스
- **감사 추적**: 모든 변경 사항 로깅 및 추적
- **버전 관리**: Git을 통한 버전 관리

### 3. 책임 있는 AI (Responsible AI)

#### 구현 사항
- **투명성**: 사용자에게 에이전트의 한계 알림
- **편향 방지**: 모델 편향 모니터링 및 완화
- **프라이버시**: 사용자 데이터 보호 및 개인정보 처리
- **규정 준수**: 관련 법규 및 표준 준수

### 4. 사용자 확인 메커니즘

#### 확인이 필요한 작업
- 데이터 변경 작업
- 권한 사용 작업
- 외부 시스템 호출
- 민감한 정보 접근

---

## 운영 및 모니터링

### 1. 로깅 및 추적

#### 필수 로그 항목
- **Agent Invocation**: 에이전트 호출 로그
- **Trace Events**: 전체 워크플로우 추적
- **Tool Invocation**: 도구 호출 내역
- **프롬프트 및 응답**: 입력/출력 로깅

#### 구현 방법
- CloudWatch Logs를 통한 중앙 집중식 로깅
- X-Ray를 통한 분산 추적
- OpenTelemetry를 통한 표준화된 관찰성

#### End-to-End Tracing (Web → API → Agent → Tool/DB → LLM)
- **추적 대상 경로**: 웹 클라이언트 → API Gateway/ALB → Application 서버/Agent Orchestrator(Lambda/ECS/EKS) → Tool/DB(DynamoDB, RDS, OpenSearch 등) → LLM/Bedrock Runtime
- **추적 구현 패턴**:
  - **Trace Context 전파**: `traceparent`(W3C Trace Context)를 HTTP/gRPC 헤더로 모든 호출 체인에 전파
  - **OpenTelemetry SDK/ADOT 사용**: Web/API/Agent 서비스에 AWS Distro for OpenTelemetry(ADOT) SDK/Collector를 적용하여 Span 생성
  - **AWS SDK 인스트루멘테이션**: DynamoDB, RDS, OpenSearch, Secrets Manager, Bedrock Runtime 호출을 각각 하위 Span으로 기록
  - **Export 대상**: ADOT Collector에서 X-Ray/CloudWatch Logs/CloudWatch Metrics로 Export
- **권장 구조**:
  1. **Web**: 프론트엔드에서 초기 Trace 시작 및 `traceparent` 헤더 설정
  2. **API**: API Gateway/ALB에서 Trace Context 수신 후 애플리케이션으로 전달
  3. **Agent Orchestrator**: Lambda/ECS/EKS에서 Agent 로직 실행 및 Tool/DB/Bedrock 호출 시 하위 Span 생성
  4. **Tool/DB/LLM**: AWS SDK 인스트루멘테이션으로 각 서비스 호출 추적
  5. **X-Ray 뷰**: 전체 호출 체인을 Service Map/Trace 뷰로 시각화

#### 참고 자료 (Tracing)
- [AWS Distro for OpenTelemetry](https://aws-otel.github.io)
- [AWS X-Ray와 OpenTelemetry 통합](https://docs.aws.amazon.com/xray/latest/devguide/otel-integration.html)
- [AWS Prescriptive Guidance - Generative AI Agents 보안/감시](https://docs.aws.amazon.com/ko_kr/prescriptive-guidance/latest/security-reference-architecture/gen-ai-agents.html)

### 2. 모니터링 지표

#### 핵심 지표
- **Latency**: 응답 시간, 각 단계별 지연 시간
- **Error Rate**: 에러 발생률, 실패율
- **Tool Failure Rate**: 도구 호출 실패율
- **Cost**: 토큰 사용량, 비용 추적
- **정확도**: 응답 품질, 사용자 만족도

#### 알림 설정
- 임계값 기반 알림
- 이상 징후 탐지
- 자동화된 대응 메커니즘

### 3. 테스트 및 평가

#### 테스트 전략
- **단위 테스트**: 개별 컴포넌트 테스트
- **통합 테스트**: 전체 워크플로우 테스트
- **A/B 테스트**: 프롬프트 및 모델 비교 테스트
- **시나리오 테스트**: 다양한 사용자 시나리오 테스트

#### 평가 지표
- 정확도, 관련성, 완전성
- 응답 시간, 처리량
- 비용 효율성
- 사용자 만족도

### 4. 인프라 자동화 (IaC)

#### 권장 사항
- **CloudFormation / CDK**: 인프라 코드화
- **Terraform**: 멀티 클라우드 지원
- **재현성**: 동일한 환경 재현 가능
- **버전 관리**: 인프라 변경 사항 추적

### 5. LLMOps / AgentOps 파이프라인

#### 파이프라인 구성 요소

**1. 개발 (Development)**
- **프롬프트 개발**: Bedrock Console 또는 API를 통한 프롬프트 템플릿 개발
- **Agent 정의**: Action Groups, Knowledge Base 설정
- **로컬 테스트**: Bedrock Runtime API를 통한 로컬 테스트

**2. 버전 관리 (Versioning)**
- **Agent 버전 관리**: Bedrock Agent 버전 관리 기능 활용
- **프롬프트 템플릿 버전 관리**: Git을 통한 프롬프트 템플릿 버전 관리
- **Knowledge Base 버전 관리**: S3 데이터 소스 버전 관리

**3. 평가 (Evaluation)**
- **자동화된 평가**: Lambda 함수를 통한 평가 파이프라인
- **평가 지표**: Hallucination, Policy Violation, 사용자 만족도
- **A/B 테스트**: 여러 Agent 버전 비교 테스트

**4. CI/CD 파이프라인**
- **CodePipeline / CodeBuild**: Agent 배포 자동화
- **테스트 단계**: 단위 테스트, 통합 테스트, 평가 테스트
- **배포 단계**: 스테이징 → 프로덕션 배포
- **롤백 메커니즘**: 자동 롤백 및 알림

**5. 모니터링 및 추적**
- **CloudWatch**: Latency, Token Usage, Cost 모니터링
- **X-Ray**: Request ID, Agent Thought Chain ID, Tool Invocation ID 추적
- **CloudWatch Logs**: 프롬프트, 응답, 도구 호출 로깅

#### 파이프라인 워크플로우
```
Source Code (Git)
    ↓
CodeBuild (Build & Test)
    ├── Unit Tests
    ├── Integration Tests
    └── Evaluation Tests
    ↓
Artifact Storage (S3)
    ↓
CodeDeploy (Deployment)
    ├── Staging Environment
    └── Production Environment
    ↓
CloudWatch (Monitoring)
    ├── Latency Tracking
    ├── Cost Monitoring
    └── Error Tracking
```

#### 참고 자료
- [Amazon Bedrock Agents Developer Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/agents.html)
- [AWS CodePipeline Documentation](https://docs.aws.amazon.com/codepipeline/)
- [AWS SageMaker MLOps Guide](https://docs.aws.amazon.com/sagemaker/latest/dg/model-registry.html)
- [AWS 생성형 AI 에이전트 보안 아키텍처](https://docs.aws.amazon.com/ko_kr/prescriptive-guidance/latest/security-reference-architecture/gen-ai-agents.html)

---

## 아키텍처 구성도

### 전체 아키텍처 개요

다음은 AWS AI Agent Architecture의 전체 구성도를 나타냅니다:

```mermaid
graph TB
    subgraph "Client Layer"
        User[사용자]
        WebApp[웹 애플리케이션]
    end
    
    subgraph "API Gateway & Security"
        APIGW[API Gateway]
        IAM[AWS IAM<br/>인증/권한]
        Guardrails[Bedrock Guardrails<br/>콘텐츠 필터링]
    end
    
    subgraph "Orchestration Layer"
        LambdaOrch[Lambda<br/>오케스트레이션]
        StepFunc[Step Functions<br/>워크플로우 관리]
        EventBridge[EventBridge<br/>이벤트 라우팅]
    end
    
    subgraph "AI/ML Layer"
        Bedrock[Amazon Bedrock<br/>Foundation Models]
        BedrockAgent[Bedrock Agents<br/>에이전트 런타임]
        SageMaker[SageMaker<br/>커스텀 모델]
    end
    
    subgraph "Knowledge & Tools"
        KB[Bedrock Knowledge Bases<br/>RAG 지식 기반]
        OpenSearch[OpenSearch<br/>벡터 검색]
        ActionGroup[Action Groups<br/>도구 정의]
        LambdaTools[Lambda Functions<br/>도구 실행]
    end
    
    subgraph "Storage & Memory"
        DynamoDB[(DynamoDB<br/>세션 상태)]
        S3[(S3<br/>문서 저장)]
        RDS[(RDS/Aurora<br/>관계형 데이터)]
    end
    
    subgraph "Observability"
        CloudWatch[CloudWatch<br/>로깅/모니터링]
        XRay[X-Ray<br/>분산 추적]
    end
    
    User --> WebApp
    WebApp --> APIGW
    APIGW --> IAM
    IAM --> Guardrails
    Guardrails --> LambdaOrch
    LambdaOrch --> StepFunc
    StepFunc --> BedrockAgent
    BedrockAgent --> Bedrock
    BedrockAgent --> KB
    BedrockAgent --> ActionGroup
    KB --> OpenSearch
    KB --> S3
    ActionGroup --> LambdaTools
    BedrockAgent --> DynamoDB
    LambdaTools --> RDS
    LambdaOrch --> CloudWatch
    BedrockAgent --> CloudWatch
    LambdaTools --> XRay
    
    style Bedrock fill:#FFD699
    style BedrockAgent fill:#FFD699
    style KB fill:#FFD699
    style Guardrails fill:#FFD699
    style LambdaOrch fill:#FFD699
    style StepFunc fill:#FFD699
```

### 레이어별 상세 구성도

```mermaid
graph LR
    subgraph "1. Foundation Models Layer"
        FM1[Claude<br/>Anthropic]
        FM2[Llama<br/>Meta]
        FM3[Titan<br/>AWS]
        FM4[Custom Model<br/>SageMaker]
    end
    
    subgraph "2. Orchestration Layer"
        O1[Bedrock Agents<br/>에이전트 오케스트레이션]
        O2[Lambda<br/>비즈니스 로직]
        O3[Step Functions<br/>복잡한 워크플로우]
    end
    
    subgraph "3. Tools & Actions Layer"
        T1[Lambda Functions<br/>계산/변환]
        T2[API Gateway<br/>외부 API]
        T3[OpenAPI<br/>도구 정의]
    end
    
    subgraph "4. Knowledge Base Layer"
        K1[Bedrock KB<br/>벡터 임베딩]
        K2[OpenSearch<br/>유사도 검색]
        K3[S3<br/>원본 문서]
    end
    
    subgraph "5. Memory & State Layer"
        M1[DynamoDB<br/>세션 상태]
        M2[S3<br/>장기 메모리]
        M3[Vector DB<br/>컨텍스트 저장]
    end
    
    FM1 --> O1
    FM2 --> O1
    FM3 --> O1
    FM4 --> O1
    O1 --> O2
    O2 --> O3
    O3 --> T1
    O3 --> T2
    O1 --> K1
    K1 --> K2
    K2 --> K3
    O1 --> M1
    M1 --> M2
    M2 --> M3
```

### 데이터 흐름도

```mermaid
sequenceDiagram
    participant User as 사용자
    participant API as API Gateway
    participant Guard as Guardrails
    participant Orch as Lambda Orchestration
    participant Agent as Bedrock Agent
    participant KB as Knowledge Base
    participant Tool as Action Group
    participant DB as DynamoDB
    participant Watch as CloudWatch
    
    User->>API: 요청 전송
    API->>Guard: 콘텐츠 검증
    Guard->>Orch: 검증된 요청
    Orch->>Agent: 에이전트 호출
    Agent->>KB: 지식 검색 (RAG)
    KB-->>Agent: 검색 결과 + 인용
    Agent->>Tool: 도구 선택 및 실행
    Tool-->>Agent: 실행 결과
    Agent->>DB: 세션 상태 저장
    Agent->>Watch: 로깅
    Agent-->>Orch: 응답 생성
    Orch-->>API: 최종 응답
    API-->>User: 응답 반환
```

---

## Application ↔ Agent 통신 패턴

### 1. 동기 호출 패턴 (Synchronous Invocation)

#### InvokeAgent API
- **용도**: 즉시 응답이 필요한 사용자 인터랙션, 짧은 처리 시간(< 5초) 작업
- **특징**: 
  - Streaming 응답 지원으로 실시간 피드백 제공
  - 세션 ID 기반 상태 유지
  - 단일 요청-응답 사이클
- **사용 사례**:
  - 채팅 인터페이스
  - 간단한 질의응답
  - 빠른 정보 검색

#### 구현 예시
```python
# Bedrock Runtime API를 통한 동기 호출
response = bedrock_runtime.invoke_agent(
    agentId=agent_id,
    agentAliasId=alias_id,
    sessionId=session_id,
    inputText=user_message
)
```

#### 장점
- 낮은 지연 시간
- 실시간 사용자 피드백
- 간단한 구현

#### 단점
- 타임아웃 제약 (일반적으로 30초 이내)
- 장시간 작업에 부적합
- 동시 요청 처리 제한

### 2. 비동기 호출 패턴 (Asynchronous Invocation)

#### EventBridge 기반 비동기 처리
- **용도**: 장시간 실행 작업, 멀티스텝 워크플로우, 백그라운드 작업
- **특징**:
  - 이벤트 기반 아키텍처
  - 확장 가능한 처리
  - 작업 상태 추적 가능
- **사용 사례**:
  - 복잡한 분석 작업
  - 리포트 생성
  - 배치 처리
  - 이메일 발송

#### Step Functions를 통한 워크플로우 오케스트레이션
- **용도**: 복잡한 멀티스텝 에이전트 작업
- **특징**:
  - 상태 머신 기반 워크플로우
  - 에러 처리 및 재시도 자동화
  - 각 단계별 상태 추적
- **구성 요소**:
  - Lambda 함수 (전처리, 후처리)
  - Bedrock Agent 호출
  - Action Group 실행
  - 상태 저장 (DynamoDB)

#### 구현 예시
```python
# EventBridge를 통한 비동기 작업 트리거
eventbridge.put_events(
    Entries=[{
        'Source': 'application.agent',
        'DetailType': 'AgentTask',
        'Detail': json.dumps({
            'agentId': agent_id,
            'input': user_message,
            'sessionId': session_id
        })
    }]
)
```

#### 장점
- 장시간 작업 처리 가능
- 확장성 및 안정성
- 복잡한 워크플로우 관리

#### 단점
- 구현 복잡도 증가
- 상태 관리 필요
- 실시간 피드백 어려움

### 3. 하이브리드 패턴 (Hybrid Pattern)

#### 동기 + 비동기 조합
- **패턴**: 초기 응답은 동기로, 장시간 작업은 비동기로 처리
- **구현**:
  1. 사용자 요청 → 동기 호출로 즉시 응답 (예: "작업을 시작했습니다")
  2. 실제 작업 → EventBridge로 비동기 처리
  3. 완료 시 → 사용자에게 알림 (SNS, WebSocket 등)

#### 사용 사례
- 문서 분석 및 요약
- 복잡한 데이터 처리
- 멀티 에이전트 협업 작업

### 4. 통신 패턴 선택 기준

#### 동기 호출이 적합한 경우
- ✅ 즉시 응답이 필요한 사용자 인터랙션
- ✅ 짧은 처리 시간 (< 5초)
- ✅ 실시간 피드백이 중요한 UX
- ✅ 단순한 질의응답

#### 비동기 호출이 적합한 경우
- ✅ 장시간 실행 작업 (분석, 리포트 생성)
- ✅ 멀티스텝 워크플로우 (여러 Tool 호출 필요)
- ✅ 백그라운드 작업 (이메일 발송, 데이터 처리)
- ✅ 배치 처리 작업

### 5. 세션 및 상태 관리

#### 세션 ID 기반 상태 유지
- **DynamoDB**: 세션 상태 저장
- **세션 만료**: TTL 설정으로 자동 정리
- **상태 동기화**: 동기/비동기 작업 간 상태 공유

#### 구현 고려사항
- 세션 키 설계 (사용자 ID + 세션 ID)
- 상태 일관성 보장
- 세션 복구 메커니즘

### 6. 에러 처리 및 Fallback

#### 동기 호출 에러 처리
- 타임아웃 처리
- 재시도 로직 (Exponential Backoff)
- Fallback 응답 제공

#### 비동기 호출 에러 처리
- Dead Letter Queue (DLQ) 활용
- Step Functions의 에러 핸들링
- 사용자 알림 메커니즘

### 7. 참고 자료

- [Amazon Bedrock Agents API Reference](https://docs.aws.amazon.com/bedrock/latest/APIReference/API_agent-runtime_InvokeAgent.html)
- [AWS Event-Driven Architecture](https://aws.amazon.com/architecture/event-driven/)
- [AWS Step Functions Best Practices](https://docs.aws.amazon.com/step-functions/latest/dg/best-practices.html)
- [Amazon Bedrock Agents Developer Guide](https://docs.aws.amazon.com/bedrock/latest/userguide/agents.html)

---

## 참고 아키텍처 패턴

### 1. Grounded Agent Workflow Pattern

```
사용자 입력
    ↓
Intent Parsing / 전처리 (Lambda)
    ↓
지식 기반 검색 (Bedrock Knowledge Bases)
    ↓
도구 선택 (Action Group)
    ↓
추론 (LLM via Bedrock)
    ↓
응답 생성 + 인용 + 확인
    ↓
로깅 & 추적
```

### 2. 서버리스 AI 아키텍처

```
API Gateway
    ↓
Lambda (인증/권한)
    ↓
Step Functions (워크플로우 오케스트레이션)
    ├── Lambda (전처리)
    ├── Bedrock (추론)
    ├── Lambda (도구 호출)
    └── Lambda (후처리)
    ↓
DynamoDB (상태 저장)
S3 (데이터 저장)
CloudWatch (모니터링)
```

### 3. 멀티 에이전트 협업 패턴

```
Coordinator Agent
    ├── Search Agent → Knowledge Base
    ├── Analysis Agent → Data Processing
    ├── Execution Agent → External APIs
    └── Validation Agent → Result Verification
    ↓
Shared Memory (DynamoDB / S3)
```

---

## 체크리스트

### 설계 단계
- [ ] Agent 스코프 및 역할 명확히 정의
- [ ] 모듈화된 아키텍처 설계
- [ ] 보안 요구사항 정의
- [ ] 모니터링 및 로깅 전략 수립

### 구현 단계
- [ ] IAM 최소 권한 원칙 적용
- [ ] Guardrails 설정
- [ ] Knowledge Base 구축
- [ ] Action Groups 정의 및 구현
- [ ] 에러 처리 및 Fallback 메커니즘 구현

### 운영 단계
- [ ] 모니터링 대시보드 구축
- [ ] 알림 설정
- [ ] 정기적인 보안 검토
- [ ] 성능 최적화
- [ ] 비용 모니터링

---

## 참고 자료

- [AWS Prescriptive Guidance - Agentic AI](https://docs.aws.amazon.com/prescriptive-guidance/latest/agentic-ai-foundations/generative-ai-agents.html)
- [AWS Architecture Blog - Resilient Generative AI Agents](https://aws.amazon.com/blogs/architecture/build-resilient-generative-ai-agents/)
- [Amazon Bedrock Best Practices](https://aws.amazon.com/blogs/machine-learning/best-practices-for-building-robust-generative-ai-applications-with-amazon-bedrock-agents-part-1/)
- [AWS Security Reference Architecture - Generative AI](https://docs.aws.amazon.com/prescriptive-guidance/latest/security-reference-architecture-generative-ai/gen-ai-agents.html)

