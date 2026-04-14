FROM eclipse-temurin:21-jdk

# JAR 파일의 경로를 ARG로 선언
ARG JAR_FILE=build/libs/dnfproject-1.0-SNAPSHOT.jar

# JASYPT_KEY 값을 ARG로 받아서 환경 변수로 설정
ARG JASYPT_KEY
ENV JASYPT_KEY=${JASYPT_KEY}

# 브라우저 저장 위치
ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright

# Chromium 실행에 필요한 최소 라이브러리
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    unzip \
    ca-certificates \
    fonts-liberation \
    libnss3 \
    libatk-bridge2.0-0 \
    libxss1 \
    libgtk-3-0 \
    libgbm1 \
    libasound2t64 \
    && rm -rf /var/lib/apt/lists/*

# Java Playwright로 Chromium 설치
RUN java -cp app.jar com.microsoft.playwright.CLI install chromium

# JAR 파일을 컨테이너로 복사
COPY ${JAR_FILE} app.jar

# 환경 변수 JASYPT_KEY를 JVM 시스템 프로퍼티로 전달
ENTRYPOINT ["sh", "-c", "java -DJASYPT_KEY=$JASYPT_KEY -jar app.jar"]
