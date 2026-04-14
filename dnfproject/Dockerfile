FROM eclipse-temurin:21-jdk

# JAR 파일의 경로를 ARG로 선언
ARG JAR_FILE=build/libs/dnfproject-1.0-SNAPSHOT.jar

# JASYPT_KEY 값을 ARG로 받아서 환경 변수로 설정
ARG JASYPT_KEY
ENV JASYPT_KEY=${JASYPT_KEY}

# Playwright 실행에 필요한 Ubuntu 런타임 의존성만 설치
RUN apt-get update && apt-get install -y --no-install-recommends \
    libglib2.0-0 \
    libnss3 \
    libnspr4 \
    libdbus-1-3 \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libdrm2 \
    libatspi2.0-0 \
    libx11-6 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxrandr2 \
    libgbm1 \
    libxcb1 \
    libxkbcommon0 \
    libpango-1.0-0 \
    libcairo2 \
    libasound2t64 \
    && rm -rf /var/lib/apt/lists/*

# JAR 파일을 컨테이너로 복사
COPY ${JAR_FILE} app.jar

# 환경 변수 JASYPT_KEY를 JVM 시스템 프로퍼티로 전달
ENTRYPOINT ["sh", "-c", "java -DJASYPT_KEY=$JASYPT_KEY -jar app.jar"]
