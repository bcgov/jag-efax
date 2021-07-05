package ca.bc.gov.ag.efax.mail.service.parser;

import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionMainProcessProcessUpdate;

public interface EmailVisitor {

    public void apply(String subject, String body, DocumentDistributionMainProcessProcessUpdate response);
    
}
