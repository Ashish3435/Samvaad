FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline

COPY src src

RUN ./mvnw clean package -DskipTests

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "target/Samvaad-0.0.1-SNAPSHOT.jar"]