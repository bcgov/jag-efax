#############################################################################################
###              Stage where Docker is building spring boot app using maven               ###
#############################################################################################
FROM maven:3.8.1-openjdk-8 as build

ARG SKIP_TESTS=false
ARG ENABLE_SPLUNK=false

WORKDIR /

COPY . .

RUN echo ENABLE_SPLUNK=${ENABLE_SPLUNK}

RUN mvn -B clean package \
        -Dtarget.fileName=jag-efax \
        -Dmaven.test.skip=${SKIP_TESTS} \
        -Denable-splunk=${ENABLE_SPLUNK}

#############################################################################################
### Stage where Docker is running a java process to run a service built in previous stage ###
#############################################################################################
FROM openjdk:8-jdk-slim

ARG MODULE=

COPY --from=build ./target/jag-efax.jar /app/service.jar

CMD ["java", "-jar", "/app/service.jar"]
#############################################################################################
