# Egoeng

영어 학습을 위한 종합 플랫폼 애플리케이션입니다. 단어, 문장 정렬, 번역, 퀴즈, 학습 진도 추적 등 다양한 기능을 제공합니다.

## 📋 프로젝트 개요

- **언어**: Java 21
- **프레임워크**: Spring Boot 3.3.4
- **데이터베이스**: PostgreSQL
- **빌드 도구**: Gradle

## 화면 구성
|Screen #1|Screen #2|
|:---:|:---:|

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

**이슈**: 외부 API(Papago)와의 통신 시 Form-urlencoded 형식의 요청이 필요했으나, 기본 Feign 설정으로는 지원 안 함

**해결 과정**:
- `feign-form` 라이브러리 추가로 멀티파트/폼 데이터 지원
- `PapagoFeignConfig`에서 `FormEncoder` 빈 설정
- `@PostMapping`의 `consumes` 속성을 `APPLICATION_FORM_URLENCODED_VALUE`로 지정

```java
@FeignClient(
    name = "papagoClient",
    url = "${papago.base-url}",
    configuration = PapagoFeignConfig.class
)
public interface TranslatePapagoClient {
    @PostMapping(
        value = "/nmt/v1/translation",
        consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    PapagoTranslationResponse translate(...);
}
```

---


### 2️⃣ QueryDSL을 이용한 동적 쿼리 생성

**이슈**: 다양한 필터 조건(카테고리, 난이도, 학습 상태 등)에 따라 동적 쿼리 작성 필요

**해결 과정**:
- `UserQuizQuerydslRepository` 구현으로 복잡한 조건 처리
- `BooleanBuilder`를 활용한 선택적 where 절 생성
- `QuizDsl` DTO로 복잡한 쿼리 결과를 계층화된 데이터로 변환

```java
BooleanBuilder builder = new BooleanBuilder();
if (category != null) {
    builder.and(quiz.category.eq(category));
}
if (level != null) {
    builder.and(userQuiz.level.goe(level));
}
return queryFactory.select(...)
    .from(userQuiz)
    .where(builder)
    .fetch();
```

---

### 3️⃣ Spring WebFlux를 이용한 비동기 처리

**이슈**: 대량의 알림 발송, LLM API 호출 등 시간이 오래 걸리는 작업 처리

**해결 과정**:
- `WebClientConfig`에서 `WebClient` 빈 생성으로 비동기 HTTP 통신
- `@EnableScheduling`으로 배경 작업 스케줄링
- 외부 API 호출 시 타임아웃 및 재시도 로직 추가

---

### 4️⃣ 다양한 퀴즈 유형 지원 (다형성 구조)

**이슈**: 단어 선택형, 문장 배열형, 번역형 등 여러 퀴즈 유형을 유연하게 처리

**해결 과정**:
- `QuizDsl` 추상화로 모든 퀴즈 타입을 통일된 형식으로 표현
- 퀴즈 타입별 `body` 객체 (`WordBody`, `SentenceArrangeBody`, `TranslateBody` 등)
- 자바 다형성으로 런타임에 적절한 타입으로 역직렬화

```java
@Getter @Builder
public class QuizDsl {
    private String type;      // "WORD", "SENTENCE_ARRANGE", "TRANSLATE"
    private String version;
    private QuizMeta meta;
    private Object body;      // 타입별 body 객체
}
```

---

###  5️⃣ Google Cloud SQL 연동 및 Secret Manager 통합

**이슈**: 프로덕션 환경에서 데이터베이스 접근 보안 및 자격증명 관리

**해결 과정**:
- `google-cloud-sql` 라이브러리로 Private IP 연결
- Google Secret Manager에서 런타임에 비밀 값 주입
- 환경별 설정 파일 (`application-prod.yml`) 분리

```yaml
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/egoeng
    driver-class-name: org.postgresql.Driver
```

