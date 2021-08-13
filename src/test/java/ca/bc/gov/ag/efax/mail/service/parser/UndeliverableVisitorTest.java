package ca.bc.gov.ag.efax.mail.service.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionMainProcessProcessResponse;

public class UndeliverableVisitorTest extends BaseTestSuite {

    @Test
    void testApply_Success() throws Exception {
        UndeliverableVisitor visitor = new UndeliverableVisitor();
        String subject = "Success: <jobId>1234</jobId><uuid>aabb</uuid>";
        DocumentDistributionMainProcessProcessResponse response = new DocumentDistributionMainProcessProcessResponse();
        visitor.apply(subject, "", response);

        // this Undeliverable matcher should not match
        assertNull(response.getJobId());
        assertNull(response.getStatusCode());
        assertNull(response.getStatusMessage());
    }

    @Test
    void testApply_Undeliverable() throws Exception {
        UndeliverableVisitor visitor = new UndeliverableVisitor();
        String subject = "Undeliverable: <jobId>1234</jobId><uuid>aabb</uuid>";
        DocumentDistributionMainProcessProcessResponse response = new DocumentDistributionMainProcessProcessResponse();
        visitor.apply(subject, "", response);

        // should match FAXListenFault
        FAXSendFault fault = new FAXSendFault();
        assertEquals("1234", response.getJobId()); // should have extracted the jobId from the subject
        assertEquals(fault.getFaultCode(), response.getStatusCode());
        assertEquals(fault.getFaultMessage(), response.getStatusMessage());
    }

    @Test
    void testApply_Mismatch() throws Exception {
        UndeliverableVisitor visitor = new UndeliverableVisitor();
        String subject = "Undeliverable: <jobId>1234</jobId>";
        DocumentDistributionMainProcessProcessResponse response = new DocumentDistributionMainProcessProcessResponse();
        visitor.apply(subject, "", response);

        // should not match regex
        assertNull(response.getJobId());
        assertNull(response.getStatusCode());
        assertNull(response.getStatusMessage());
    }
}
