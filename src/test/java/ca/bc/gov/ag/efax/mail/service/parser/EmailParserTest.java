package ca.bc.gov.ag.efax.mail.service.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.ws.exception.FAXListenFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionMainProcessProcessResponse;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

public class EmailParserTest extends BaseTestSuite {

    @Autowired
    private EmailParser emailParser;
    
    @Test
    void testParse_1() throws Exception {
        EmailMessage emailMessage = new EmailMessage(exchangeService);
        emailMessage.setSubject("Test subject");
        emailMessage.setBody(MessageBody.getMessageBodyFromText("Test body"));
                
        DocumentDistributionMainProcessProcessResponse response = emailParser.parse(emailMessage);
        
        // not jobId should have been found
        assertNull(response.getJobId());
    }

    @Test
    void testParse_2() throws Exception {
        EmailMessage emailMessage = new EmailMessage(exchangeService);
        emailMessage.setSubject("Undeliverable: <jobId>1234</jobId><uuid>aabb</uuid>");
        emailMessage.setBody(MessageBody.getMessageBodyFromText("Test body"));
                
        DocumentDistributionMainProcessProcessResponse response = emailParser.parse(emailMessage);

        FAXListenFault fault = new FAXListenFault();
        assertEquals("1234", response.getJobId()); // should have extracted the jobId from the subject
        assertEquals(fault.getFaultCode(), response.getStatusCode());
        assertEquals(fault.getFaultMessage(), response.getStatusMessage());
    }
}
