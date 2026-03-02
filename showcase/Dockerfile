FROM amazoncorretto:21-alpine-jdk
WORKDIR /app
COPY target/*.jar app.jar
ENV REDIS_HOST=redis
ENV PAYMENT_HOST=payment
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]