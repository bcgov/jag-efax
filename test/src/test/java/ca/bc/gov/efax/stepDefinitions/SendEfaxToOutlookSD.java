package ca.bc.gov.efax.stepDefinitions;

import ca.bc.gov.efax.TestConfig;
import ca.bc.gov.efax.page.ExchangeLogInPage;
import ca.bc.gov.efax.service.SendFaxService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@CucumberContextConfiguration
@SpringBootTest(classes = TestConfig.class)
public class SendEfaxToOutlookSD {

    private final SendFaxService sendFaxService;
    private Response actualEfaxResponse;
    private final ExchangeLogInPage exchangeLogInPage;

    private final Logger logger = LoggerFactory.getLogger(SendEfaxToOutlookSD.class);

    public SendEfaxToOutlookSD(SendFaxService sendFaxService, ExchangeLogInPage exchangeLogInPage) {
        this.sendFaxService = sendFaxService;
        this.exchangeLogInPage = exchangeLogInPage;
    }

    @Given("an eFax service is available")
    public void anEfaxServiceIsAvailable() {

    }

    @When("an eFax with attachments is sent")
    public void anEfaxWithAttachmentsIsSent() throws IOException {
        actualEfaxResponse = sendFaxService.sendEfaxResponse();
        logger.info("Api response status code: {}", actualEfaxResponse.getStatusCode());

    }

    @Then("verify the eFax is sent successfully")
    public void verifyTheEfaxIsSentSuccessfully() {
        Assert.assertEquals(HttpStatus.SC_ACCEPTED, actualEfaxResponse.getStatusCode());

    exchangeLogInPage.sigInToOutlook();

    }
}
