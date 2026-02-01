FROM eclipse-temurin:21-jdk

# JAR 파일의 경로를 ARG로 선언
ARG JAR_FILE=build/libs/root-0.0.1-SNAPSHOT.jar

# JASYPT_KEY 값을 ARG로 받아서 환경 변수로 설정
ARG JASYPT_KEY
ENV JASYPT_KEY=${JASYPT_KEY}

# JAR 파일을 컨테이너로 복사
COPY ${JAR_FILE} app.jar

# 환경 변수 JASYPT_KEY를 JVM 시스템 프로퍼티로 전달
ENTRYPOINT ["sh", "-c", "java -DJASYPT_KEY=$JASYPT_KEY -jar app.jar"]