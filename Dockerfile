FROM openjdk:13-jdk-alpine
RUN mkdir -p /usr/src/myapp
WORKDIR /usr/src/myapp
EXPOSE 27017 27018 27019 8500
COPY build/libs/authentication*.jar /usr/src/myapp
RUN mv authentication*.jar authentication.jar
CMD ["java", "-jar", "authentication.jar"]
