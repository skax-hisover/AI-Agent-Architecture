# GCP AI Agent 샘플 애플리케이션

이 프로젝트는 **GCP AI Agent Architecture Best Practices** 가이드를 기반으로 한 간단한 샘플 애플리케이션입니다.

## 📋 개요

- **백엔드**: SpringBoot + Maven 기반 REST API (포트 `8082`)
- **프론트엔드**: React 기반 웹 인터페이스 (포트 `3002` 권장)
- **아키텍처 컨셉**:
  - Vertex AI Agent Engine / Multi-Agent 패턴 (Coordinator, Tools, RAG) 모킹
  - Cloud Functions / Cloud Run / Firestore / Vertex AI Search 개념 반영 (실제 호출 아님)

## 🏗️ 아키텍처 구성요소

1. **Coordinator Agent**: `AgentOrchestrationService`
2. **Search Agent 역할**: `KnowledgeBaseService` (RAG 개념 모킹)
3. **Tool/Execution Agent 역할**: `ToolService` (계산기, 날씨, 시간)
4. **Memory/Session State**: `SessionService` (Firestore/Memorystore 개념 모킹)
5. **REST API**: `AgentController`

## 🚀 실행 방법

### 사전 요구사항

- Java 17 이상
- Maven 3.6 이상
- Node.js 16 이상
- npm 또는 yarn

### 백엔드 실행

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

백엔드는 `http://localhost:8082`에서 실행됩니다.

### 프론트엔드 실행

새 터미널에서:

```bash
cd frontend
npm install
PORT=3002 npm start   # Windows에서는 set PORT=3002 && npm start
```

프론트엔드는 기본적으로 `http://localhost:3002`에서 실행됩니다.

## 📝 사용 방법

1. 브라우저에서 `http://localhost:3002` 접속
2. 다음과 같은 질문을 시도해보세요:
   - `"5 + 3 계산해줘"`
   - `"현재 날씨 알려줘"`
   - `"지금 몇 시야?"`
   - `"Vertex AI에 대해 알려줘"`
   - `"Agent Engine이 뭐야?"`

## 🔧 주요 기능

### 1. 멀티 에이전트(Coordinator 패턴) 모킹
- Coordinator 격인 `AgentOrchestrationService`가:
  - Search 역할 (`KnowledgeBaseService`)
  - Tools 역할 (`ToolService`)
  - Session 역할 (`SessionService`)
  를 오케스트레이션

### 2. RAG + 지식 기반
- GCP, Vertex AI, Agent Engine, Cloud Run, Cloud Functions, Firestore 등에 대한 기본 설명을 인메모리에 저장
- 질문에 해당 키워드가 포함되면 관련 설명을 응답 및 인용으로 반환

### 3. Tools / Actions
- **계산기**: 단순 사칙연산 (예: `"5 + 3 계산해줘"`)
- **날씨 조회**: 샘플 날씨 데이터 반환
- **시간 조회**: 서버 현재 시간 반환

### 4. 세션/메모리 관리
- 세션 ID를 통해 대화 히스토리를 인메모리로 유지
- Firestore / Memorystore 기반 설계의 축소판 개념

## 📁 프로젝트 구조

```text
sample-gcp-agent/
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/gcp/agent/
│   │       │   ├── controller/        # REST API 컨트롤러
│   │       │   ├── service/           # Orchestration, Tools, KB, Session
│   │       │   └── model/             # 요청/응답 DTO
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/                # React 컴포넌트 (Header, ChatInterface 등)
│   │   ├── App.js
│   │   └── index.js
│   ├── public/
│   │   └── index.html
│   └── package.json
└── README.md
```

## 🔐 보안 고려사항

이 샘플은 **로컬 데모** 용도로, 실제 GCP 리소스나 IAM, VPC Service Controls 등은 사용하지 않았습니다.  
프로덕션에서는:

- 서비스 계정 및 IAM 최소 권한 원칙 적용
- Secret Manager를 통한 시크릿 관리
- VPC Service Controls / Private Service Connect 적용
- Cloud Logging / Monitoring / Trace 연동

## 🎯 확장 아이디어

- Vertex AI Agent Engine + ADK를 이용한 실제 에이전트 런타임 구성
- Vertex AI Search 인덱스 연결 및 RAG 구현
- Cloud Functions / Cloud Run으로 Tools를 분리
- Firestore / BigQuery 기반 Memory Bank 구현

## 📚 참고 자료

- GCP 가이드: `../GCP_AI_Agent_Architecture_Best_Practices.md`
- [Vertex AI Agent Engine](https://cloud.google.com/agent-builder/agent-engine)
- [Multi-Agent AI System Architecture](https://cloud.google.com/architecture/multiagent-ai-system)


