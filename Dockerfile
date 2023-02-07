# -----------------------------------------
# BUILDER
# -----------------------------------------
FROM maven:3.8.3-openjdk-17 as builder

WORKDIR /home

COPY src/ src/
COPY pom.xml .

RUN mvn clean install -DskipTests=true

# -----------------------------------------
# RUNNER
# -----------------------------------------
FROM openjdk:20-ea-17-jdk

WORKDIR /home

ARG ARG_VERSION=local

ENV VERSION=${ARG_VERSION}
ENV PORT=8080
ENV LOG_PATH=/tmp/log.logs
ENV LOG_LEVEL=INFO
ENV TZ America/Argentina/Buenos_Aires

COPY --from=builder /home/target/*.jar .
COPY application.yml .

CMD java -jar *.jar