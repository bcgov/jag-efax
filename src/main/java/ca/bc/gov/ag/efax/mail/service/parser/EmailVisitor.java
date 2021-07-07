package ca.bc.gov.ag.efax.mail.service.parser;

import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionMainProcessProcessResponse;

public interface EmailVisitor {

    public void apply(String subject, String body, DocumentDistributionMainProcessProcessResponse response);
    
}
