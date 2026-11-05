FROM eclipse-temurin:17-jre
WORKDIR /app
COPY build/libs/dearseoul.jar app.jar
ENV APP_DATA_DIR=/data PORT=8080 COOKIE_SECURE=true SPRING_PROFILES_ACTIVE=prod
EXPOSE 8080
ENTRYPOINT ["java", "-Djava.awt.headless=true", "-jar", "/app/app.jar"]
