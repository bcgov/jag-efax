package ca.bc.gov.ag.efax.mail.service.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.mail.model.DocumentDistributionMainProcessProcessResponseDecorator;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;

public class FailedVisitorTest extends BaseTestSuite {

    @Test
    void testApply_Success() throws Exception {
        FailedVisitor visitor = new FailedVisitor();
        String subject = "Message Succeeded: 2505551234 (2505551234@somewhere.com) on 8/13/2021 at 12:38:15 PM";
        String body = "Your fax which was sent to 2505551234@somewhere.com at 2505551234 with subject <jobId>123</jobId><uuid>8e3b47c4-60b6-43e7-81ee- c9ac5930e49d</uuid> was delivered successfully";
        DocumentDistributionMainProcessProcessResponseDecorator response = new DocumentDistributionMainProcessProcessResponseDecorator();
        visitor.apply(subject, body, response);

        // this Failed matcher should not match
        assertNull(response.getJobId());
        assertNull(response.getStatusCode());
        assertNull(response.getStatusMessage());                
    }

    @Test
    void testApply_Failed() throws Exception {
        FailedVisitor visitor = new FailedVisitor();
        String subject = "Message Failed: 2505551234 - Failed (The transmission was disconnected while in progress.)";
        String body = "Your fax which was sent to 2505551234@somewhere.com at 2505551234 with subject <jobId>1234</jobId><uuid>070d08c0-8704-4232-b5e2- cc23b377433f</uuid> on 8/13/2021 at 11:17:41 AM, failed to be delivered";
        DocumentDistributionMainProcessProcessResponseDecorator response = new DocumentDistributionMainProcessProcessResponseDecorator();
        visitor.apply(subject, body, response);

        // should match FAXSendFault
        FAXSendFault fault = new FAXSendFault();
        assertEquals("1234", response.getJobId()); // should have extracted the jobId from the body
        assertEquals("070d08c0-8704-4232-b5e2-cc23b377433f", response.getUuid()); // should have extracted the jobId from the body
        assertEquals(fault.getFaultCode(), response.getStatusCode());
        assertEquals(fault.getFaultMessage(), response.getStatusMessage());              
    }
    
}
