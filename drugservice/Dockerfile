FROM openjdk:8-jdk
COPY ./target/drugService.jar drugService.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/drugService.jar"]