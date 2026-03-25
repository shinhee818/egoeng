# Egoeng

영어 학습을 위한 종합 플랫폼 애플리케이션입니다. 단어, 문장 정렬, 번역, 퀴즈, 학습 진도 추적 등 다양한 기능을 제공합니다.

## 📋 프로젝트 개요

- **언어**: Java 21
- **프레임워크**: Spring Boot 3.3.4
- **데이터베이스**: PostgreSQL
- **빌드 도구**: Gradle

[//]: # (|<img src="https://user-images.githubusercontent.com/80824750/208456048-acbf44a8-cd71-4132-b35a-500047adbe1c.gif" width="400"/>|<img src="https://user-images.githubusercontent.com/80824750/208456234-fb5fe434-aa65-4d7a-b955-89098d5bbe0b.gif" width="400"/>|)

## 🚀 주요 기능

- **인증/인가**: OAuth2, JWT 기반 보안 인증
- **단어 관리**: 단어 등록, 조회, 카테고리 관리
- **학습**: 개인화된 학습 경로 관리
- **퀴즈**: 다양한 유형의 퀴즈 시스템
- **채팅**: 실시간 채팅 기능
- **알림**: 예약된 알림 시스템
- **번역**: Papago API를 활용한 자동 번역
- **LLM 연동**: AI 기반 기능 지원


## 🔧 기술 스택

### Back-end
<div>
<img src="https://img.shields.io/badge/Java%2021-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
<img src="https://img.shields.io/badge/Spring%20Boot%203.3-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
<img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
<img src="https://img.shields.io/badge/Spring%20WebFlux-6DB33F?style=for-the-badge&logo=spring&logoColor=white" />
<img src="https://img.shields.io/badge/QueryDSL-0096D6?style=for-the-badge&logo=java&logoColor=white" />
<img src="https://img.shields.io/badge/OpenFeign-FF6F00?style=for-the-badge&logo=spring&logoColor=white" />
</div>

### Database
<div>
<img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" />
<img src="https://img.shields.io/badge/Google%20Cloud%20SQL-4285F4?style=for-the-badge&logo=google-cloud&logoColor=white" />
</div>

### Security
<div>
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" />
<img src="https://img.shields.io/badge/OAuth2-3C3C3D?style=for-the-badge&logo=auth0&logoColor=white" />
</div>

### Infrastructure & Tools
<div>
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
<img src="https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white" />
<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" />
</div>

<br />

## 🤔 기술적 이슈와 해결 과정

### 1️⃣ OpenFeign을 이용한 외부 API 통신 (Papago 번역)

- **Issue**: Papago API가 `application/x-www-form-urlencoded` 형식을 요구하나, 기본 Feign 설정으로는 JSON만 지원해 요청 실패.
- **Decision**: `feign-form` 라이브러리 추가 및 커스텀 Feign 설정으로 해결.
- **Implementation**: `PapagoFeignConfig`에서 `FormEncoder` 빈 설정, `@PostMapping`의 `consumes` 속성을 `APPLICATION_FORM_URLENCODED_VALUE`로 지정.
- **Result**: Papago API 정상 연동, **외부 API별 Feign 설정 분리**로 확장성 확보.


---


### 2️⃣ QueryDSL을 이용한 동적 쿼리 생성
- **Issue**: 카테고리·난이도·학습 상태 등 조건이 조합될 때마다 쿼리를 별도로 작성해야 해 코드 중복 심화.
- **Decision**: QueryDSL `BooleanBuilder`로 조건을 동적으로 조합.
- **Implementation**: `UserQuizQuerydslRepository` 구현, 조건 존재 시에만 `where` 절에 추가. `QuizDsl` DTO로 복잡한 쿼리 결과를 계층화된 구조로 변환.
- **Result**: 필터 조건 **N개 조합을 단일 메서드**로 처리, 조건 추가 시 기존 코드 수정 없이 확장 가능.


---

### 3️⃣ LLM 스트리밍 응답 처리

- **Issue**: Feign Client로 LLM 스트리밍 API 호출 시 `Flux<String>` 반환 불가. Feign은 블로킹 방식이라 SSE(Text Event Stream) 처리 불가.
- **Decision**: 일반 단건 요청은 Feign 유지, 스트리밍 전용으로 WebClient 도입.
- **Implementation**: 단건 채팅은 Feign Client로 처리(코드 단순성 유지), 스트리밍은 WebClient + `Flux<String>`으로 청크 단위 수신.
- **Result**: 스트리밍 응답 정상 처리, **용도별 클라이언트 분리**로 각 방식의 장점 유지.

---

### 4️⃣ 다양한 퀴즈 유형 지원 (다형성 구조)


- **Issue**: 단어 선택형·문장 배열형·번역형 등 퀴즈 유형마다 다른 데이터 구조를 하나의 API로 처리해야 함.
- **Decision**: 퀴즈 타입별 `body` 객체를 분리하고 `QuizDsl`로 통일된 응답 구조 설계.
- **Implementation**: `WordBody`, `SentenceArrangeBody`, `TranslateBody` 등 타입별 클래스 분리, 자바 다형성으로 런타임에 적절한 타입으로 역직렬화.
- **Result**: 신규 퀴즈 타입 추가 시 기존 코드 수정 **0건** (body 클래스 1개 추가만으로 확장).

---

###  5️⃣ Google Cloud SQL 연동 및 Secret Manager 통합

- **Issue**: 대량 알림 발송, LLM API 호출 등 응답 지연이 긴 작업을 동기 방식으로 처리 시 스레드 블로킹 발생.
- **Decision**: WebFlux + WebClient로 논블로킹 비동기 처리, `@EnableScheduling`으로 백그라운드 작업 스케줄링.
- **Implementation**: `WebClientConfig`에서 WebClient 빈 생성, 외부 API 호출 시 타임아웃(connectTimeout: 30s, readTimeout: 60s) 및 에러 핸들링 추가.
- **Result**: 알림·LLM 호출 시 **스레드 블로킹 없이** 처리, 동시 요청 처리 가능.


