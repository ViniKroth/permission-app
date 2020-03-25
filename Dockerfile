FROM openjdk:8-jdk-alpine
WORKDIR opt/app
COPY ./ /opt/app/
RUN ./gradlew clean build -x test
WORKDIR build/libs
ENTRYPOINT ["java","-jar", "permissions-0.0.1-SNAPSHOT.jar" ]
