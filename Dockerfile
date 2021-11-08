
FROM maven:3.8.3-jdk-11 AS build
RUN apt update
RUN apt install npm -y
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN cd /usr/src/app/src/main/webapp && npm install
RUN cd /usr/src/app/src/main/webapp && bash buildscript.sh
RUN mvn -f /usr/src/app/pom.xml clean package

FROM gcr.io/distroless/java
COPY --from=build /usr/src/app/target/mundane-assignment-police-0.0.1-SNAPSHOT.jar /usr/app/mundane-assignment-police-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/mundane-assignment-police-0.0.1-SNAPSHOT.jar"]
