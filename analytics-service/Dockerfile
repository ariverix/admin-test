FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S javauser && adduser -S javauser -G javauser

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

RUN chown javauser:javauser /app/app.jar
USER javauser

# Порт gRPC
EXPOSE 9090

ENTRYPOINT ["java", "-jar", "app.jar"]