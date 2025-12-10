#############################################################################################
###              Stage where Docker is building spring boot app using maven               ###
#############################################################################################
FROM maven:3.8.3-openjdk-17 as build

ARG SKIP_TESTS=false
ARG ENABLE_SPLUNK=false

WORKDIR /efax

COPY . .

RUN echo ENABLE_SPLUNK=${ENABLE_SPLUNK}

RUN mvn -B clean package \
        -P jag-efax \
        -Dtarget.fileName=jag-efax \
        -Dmaven.test.skip=${SKIP_TESTS} \
        -Denable-splunk=${ENABLE_SPLUNK}

#############################################################################################
### Stage where Docker is running a java process to run a service built in previous stage ###
#############################################################################################
FROM eclipse-temurin:17-jre-alpine

ARG MODULE=

# fix CVE-2025-65018, CVE-2025-64720, CVE-2025-66293, CVE-2025-59375, CVE-2025-9230
RUN apk update \
    && apk add --upgrade --no-cache libexpat \
    && apk add --upgrade --no-cache libpng \
    && apk add --upgrade --no-cache openssl

COPY --from=build /efax/jag-efax/target/jag-efax.jar /app/service.jar

CMD ["java", "-jar", "/app/service.jar"]
#############################################################################################
