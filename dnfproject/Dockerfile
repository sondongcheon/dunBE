FROM eclipse-temurin:21-jdk

# JAR 파일의 경로를 ARG로 선언
ARG JAR_FILE=build/libs/dnfproject-1.0-SNAPSHOT.jar

# JASYPT_KEY 값을 ARG로 받아서 환경 변수로 설정
ARG JASYPT_KEY
ENV JASYPT_KEY=${JASYPT_KEY}

# Playwright 브라우저 설치 경로 고정
ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright

# Node/npm 설치 후 Playwright Chromium과 의존 패키지 선설치
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    ca-certificates \
    gnupg \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y --no-install-recommends nodejs \
    && npx -y playwright@1.42.0 install chromium \
    && rm -rf /var/lib/apt/lists/*

# JAR 파일을 컨테이너로 복사
COPY ${JAR_FILE} app.jar

# 환경 변수 JASYPT_KEY를 JVM 시스템 프로퍼티로 전달
ENTRYPOINT ["sh", "-c", "java -DJASYPT_KEY=$JASYPT_KEY -jar app.jar"]
