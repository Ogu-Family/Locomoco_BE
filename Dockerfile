FROM openjdk:17
ARG JAR_FILE=/build/libs/*-SNAPSHOT.jar
ADD $JAR_FILE app.jar
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
