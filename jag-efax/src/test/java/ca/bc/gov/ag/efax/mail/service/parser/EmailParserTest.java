package ca.bc.gov.ag.efax.mail.service.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.graph.model.GmailMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionMainProcessProcessResponse;

public class EmailParserTest extends BaseTestSuite {

    @Autowired
    private EmailParser emailParser;
    
    @Test
    void testParse_1() throws Exception {
        GmailMessage emailMessage = new GmailMessage();
        emailMessage.setSubject("Test subject");
        emailMessage.setBody("Test body");
                
        DocumentDistributionMainProcessProcessResponse response = emailParser.parse(emailMessage);
        
        // not jobId should have been found
        assertNull(response.getJobId());
    }

    @Test
    void testParse_2() throws Exception {
        GmailMessage emailMessage = new GmailMessage();
        emailMessage.setSubject("Undeliverable: <jobId>1234</jobId><uuid>aabb</uuid>");
        emailMessage.setBody("Test body");

        DocumentDistributionMainProcessProcessResponse response = emailParser.parse(emailMessage);

        FAXSendFault fault = new FAXSendFault();
        assertEquals("1234", response.getJobId()); // should have extracted the jobId from the subject
        assertEquals(fault.getFaultCode(), response.getStatusCode());
        assertEquals(fault.getFaultMessage(), response.getStatusMessage());
    }
}
