# Stage 1: 빌드 스테이지
# 반드시 'AS build'라고 명시해야 아래에서 --from=build로 참조할 수 있습니다.
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

# 소스 코드 전체 복사 (settings.gradle, build.gradle 포함)
COPY . .

# 애플리케이션 빌드
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: 실행 스테이지
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# [중요] 이전 스테이지(build)에서 생성된 JAR 파일 복사
# !plain 패턴을 사용하여 실행 가능한 JAR만 정확히 가져옵니다.
COPY --from=build /app/build/libs/*[!plain].jar app.jar

# JVM 안정성 및 메모리 최적화 옵션
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Dfile.encoding=UTF-8"

EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
