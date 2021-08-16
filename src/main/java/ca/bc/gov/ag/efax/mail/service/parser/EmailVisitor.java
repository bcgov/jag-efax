package ca.bc.gov.ag.efax.mail.service.parser;

import ca.bc.gov.ag.efax.mail.model.DocumentDistributionMainProcessProcessResponseDecorator;

public interface EmailVisitor {

    public void apply(String subject, String body, DocumentDistributionMainProcessProcessResponseDecorator response);
    
}
