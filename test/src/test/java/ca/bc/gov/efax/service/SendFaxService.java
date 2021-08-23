package ca.bc.gov.efax.service;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SendFaxService {

    @Value("${EFAX_API_HOST:}")
    private String eFaxApiHost;

    public Response sendEfaxResponse() throws IOException {

        File file = ResourceUtils.getFile("classpath:xmlPayload.xml");

        //Read File Content
        String xmlContent = new String(Files.readAllBytes(file.toPath()));

        RequestSpecification request = RestAssured
                .given()
                .contentType("text/xml")
                .body(xmlContent);

        return request
                .when()
                .post( eFaxApiHost)
                .then()
                .extract()
                .response();

    }
}
