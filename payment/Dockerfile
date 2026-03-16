FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY target/*.jar app.jar
ENV KEYCLOAK_HOST=keycloak
ENV KEYCLOAK_PORT=8082
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]