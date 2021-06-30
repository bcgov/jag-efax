#############################################################################################
###              Stage where Docker is building spring boot app using maven               ###
#############################################################################################
FROM maven:3.6.3-jdk-8 as build

ARG SKIP_TESTS=false
ARG MODULE=

WORKDIR /

COPY . .

RUN mvn -B clean package \
        -P ${MODULE} \
        -Dmaven.test.skip=${SKIP_TESTS}


#############################################################################################
### Stage where Docker is running a java process to run a service built in previous stage ###
#############################################################################################
FROM openjdk:8-jdk-slim

ARG MODULE=

COPY --from=build ./${MODULE}/target/${MODULE}-0.0.1-SNAPSHOT.jar /app/service.jar

CMD ["java", "-jar", "/app/service.jar"]
#############################################################################################
