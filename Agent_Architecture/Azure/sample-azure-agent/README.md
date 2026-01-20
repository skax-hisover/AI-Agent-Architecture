# Azure AI Agent 샘플 애플리케이션

이 프로젝트는 **Azure AI Agent Architecture Best Practices** 가이드를 기반으로 한 간단한 샘플 애플리케이션입니다.

## 📋 개요

- **백엔드**: SpringBoot + Maven 기반 REST API (포트 `8081`)
- **프론트엔드**: React 기반 웹 인터페이스 (포트 `3000`)
- **아키텍처 컨셉**:
  - Azure OpenAI Assistants + Function Calling (모킹)
  - Azure AI Search 기반 RAG (인메모리 모킹)
  - Azure Functions / Cosmos DB / Application Insights 개념 반영

## 🏗️ 아키텍처 구성요소

1. **Orchestration Layer**: `AgentOrchestrationService`
2. **Tools / Functions**: `ToolService` (계산기, 날씨, 시간)
3. **Knowledge Base (RAG)**: `KnowledgeBaseService`
4. **Memory / Session State**: `SessionService`
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

백엔드는 `http://localhost:8081`에서 실행됩니다.

### 프론트엔드 실행

새 터미널에서:

```bash
cd frontend
npm install
npm start
```

프론트엔드는 `http://localhost:3001`에서 실행됩니다.

> **참고**: 포트 3001을 사용하려면 환경 변수를 설정하거나 `.env` 파일을 생성하세요:
> - Windows: `set PORT=3001 && npm start`
> - Linux/Mac: `PORT=3001 npm start`
> - 또는 `frontend` 폴더에 `.env` 파일을 만들고 `PORT=3001` 추가

## 📝 사용 방법

1. 브라우저에서 `http://localhost:3001` 접속
2. 다음과 같은 질문을 시도해보세요:
   - `"5 + 3 계산해줘"`
   - `"현재 날씨 알려줘"`
   - `"지금 몇 시야?"`
   - `"Azure OpenAI에 대해 알려줘"`
   - `"Azure AI Search가 뭐야?"`

## 🔧 주요 기능

### 1. 함수 호출(Function Calling) 모킹
- 메시지에서 의도를 분석해 내부 도구 선택
- 계산기 / 날씨 / 시간 등 간단한 함수 실행

### 2. RAG + 지식 기반
- Azure, Azure OpenAI, Azure AI Search, Functions, Logic Apps, RAG 등에 대한 설명을 인메모리로 보관
- 질문에 해당 키워드가 포함되면 관련 설명을 응답으로 제공

### 3. 세션 및 상태 관리
- 세션 ID를 발급하여 대화 흐름을 유지
- 인메모리 맵을 사용하여 간단한 히스토리 저장 (Cosmos DB/Redis 개념 모킹)

## 📁 프로젝트 구조

```text
sample-azure-agent/
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/azure/agent/
│   │       │   ├── controller/        # REST API 컨트롤러
│   │       │   ├── service/           # 오케스트레이션, Tools, KB, Session
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

이 샘플은 **데모 목적**으로, 실제 Azure 리소스나 네트워크 보안은 구현하지 않았습니다.  
프로덕션에서는 다음을 고려해야 합니다:

- Azure AD 기반 인증/권한 부여
- Key Vault를 통한 시크릿 관리
- Azure AI Content Safety를 통한 콘텐츠 필터링
- Private Endpoint / VNet 통합

## 🎯 확장 아이디어

- 실제 Azure OpenAI Service 연동 (API 키/엔드포인트 설정)
- Azure AI Search 인덱스 연결 및 벡터 검색 구현
- Azure Functions 호출(HTTP 트리거)로 Tools 분리
- Cosmos DB를 사용한 세션/히스토리 영속화

## 📚 참고 자료

- Azure 가이드: `../Azure_AI_Agent_Architecture_Best_Practices.md`
- [Azure OpenAI Service 문서](https://learn.microsoft.com/azure/ai-services/openai/)
- [Azure AI Search 문서](https://learn.microsoft.com/azure/search/)
- [Azure Functions Best Practices](https://learn.microsoft.com/azure/azure-functions/functions-best-practices)


