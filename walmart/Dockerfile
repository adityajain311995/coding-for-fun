FROM openjdk:8 AS builder
COPY . .
RUN ./gradlew build

FROM openjdk:8
WORKDIR /walmart-app
COPY --from=builder ./build/libs/fun-coding-walmart.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
