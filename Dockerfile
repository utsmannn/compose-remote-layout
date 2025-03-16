FROM eclipse-temurin:17-jdk

RUN apt-get update && apt-get install -y curl unzip

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

EXPOSE 8080

CMD ["./gradlew", ":jsonBuilderWeb:jsBrowserRun", "--continuous", "--args='--host=0.0.0.0'"]