FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN (mvn clean package -DskipTests -B -e -V \
    -Dmaven.wagon.http.retryHandler.count=10 \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -Dmaven.wagon.http.pool=false \
    -Dhttp.keepAlive=false) || \
    (sleep 10 && mvn clean package -DskipTests -B -e -V \
    -Dmaven.wagon.http.retryHandler.count=10 \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
    -Dmaven.wagon.http.pool=false \
    -Dhttp.keepAlive=false)

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Security: Update packages and run as non-root user
RUN (apk update || true) && (apk upgrade || true) && \
    addgroup -S appgroup && adduser -S appuser -G appgroup && \
    chown -R appuser:appgroup /app
USER appuser

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8083
CMD ["java", "-jar", "app.jar"]
