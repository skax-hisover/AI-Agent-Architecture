# AWS AI Agent 샘플 애플리케이션

이 프로젝트는 AWS AI Agent Architecture Best Practices 가이드를 기반으로 한 간단한 샘플 애플리케이션입니다.

## 📋 개요

- **백엔드**: SpringBoot + Maven 기반 REST API
- **프론트엔드**: React 기반 웹 인터페이스
- **아키텍처**: AWS AI Agent Architecture Best Practices 패턴 적용

## 🏗️ 아키텍처 구성요소

이 샘플은 다음 AWS AI Agent 아키텍처 요소를 구현합니다:

1. **Orchestration Layer**: `AgentOrchestrationService` - 다단계 작업 조정
2. **Tools / Action Groups**: `ToolService` - 계산기, 날씨, 시간 조회 등
3. **Knowledge Base**: `KnowledgeBaseService` - RAG를 위한 지식 검색
4. **Memory / Session State**: `SessionService` - 세션 관리 및 대화 히스토리
5. **Observability**: 로깅 및 추적 (SpringBoot 기본 로깅)

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

백엔드는 `http://localhost:8080`에서 실행됩니다.

### 프론트엔드 실행

새 터미널에서:

```bash
cd frontend
npm install
npm start
```

프론트엔드는 `http://localhost:3000`에서 실행됩니다.

## 📝 사용 방법

1. 브라우저에서 `http://localhost:3000` 접속
2. 다음과 같은 질문을 시도해보세요:
   - "5 + 3 계산해줘"
   - "현재 날씨 알려줘"
   - "지금 몇 시야?"
   - "AWS에 대해 알려줘"
   - "Bedrock이 뭐야?"

## 🔧 주요 기능

### 1. 도구 호출 (Tools)
- **계산기**: 수학 연산 수행
- **날씨 조회**: 현재 날씨 정보 (모킹 데이터)
- **시간 조회**: 현재 시간 표시

### 2. 지식 기반 검색 (Knowledge Base)
- AWS, Bedrock, Lambda, Agent, RAG 등에 대한 정보 제공
- RAG 패턴 구현 (검색 결과를 응답에 포함)

### 3. 세션 관리
- 대화 세션 유지
- 대화 히스토리 저장

### 4. 관찰성 (Observability)
- 요청/응답 로깅
- 도구 사용 추적
- 메타데이터 제공

## 📁 프로젝트 구조

```
sample-aws-agent/
├── backend/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/aws/agent/
│   │       │   ├── controller/     # REST API 컨트롤러
│   │       │   ├── service/        # 비즈니스 로직
│   │       │   └── model/          # 데이터 모델
│   │       └── resources/
│   │           └── application.properties
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/             # React 컴포넌트
│   │   ├── App.js
│   │   └── index.js
│   └── package.json
└── README.md
```

## 🔐 보안 고려사항

이 샘플은 데모 목적이므로 기본적인 보안만 구현되어 있습니다. 실제 프로덕션 환경에서는:

- 인증/인가 구현 (JWT, OAuth 등)
- 입력 검증 강화
- Guardrails 구현
- 암호화 적용
- Rate Limiting 구현

## 🎯 향후 개선 사항

- 실제 Amazon Bedrock 통합
- DynamoDB를 사용한 영구 세션 저장
- 실제 날씨 API 연동
- 더 많은 도구 추가
- 멀티 에이전트 협업 패턴 구현

## 📚 참고 자료

- [AWS AI Agent Architecture Best Practices](../AWS_AI_Agent_Architecture_Best_Practices.md)
- [Spring Boot 공식 문서](https://spring.io/projects/spring-boot)
- [React 공식 문서](https://react.dev/)

## 📄 라이선스

이 샘플 코드는 교육 및 데모 목적으로 제공됩니다.
