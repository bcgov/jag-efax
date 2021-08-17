package ca.bc.gov.ag.efax.mail.service.parser;

import java.util.regex.Matcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.ag.efax.mail.model.DocumentDistributionMainProcessProcessResponseDecorator;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;

public class UndeliverableVisitor extends EmailVisitor {

    private final static Logger logger = LoggerFactory.getLogger(UndeliverableVisitor.class);
    
    /** A regex pattern for the expected subject line of an undeliverable fax */
    private static final String UNDELIVERABLE = "Undeliverable: <jobId>(\\d+)</jobId><uuid>(.+)</uuid>";

    @Override
    public void apply(String subject, String body, DocumentDistributionMainProcessProcessResponseDecorator response) {
        FAXSendFault fault = new FAXSendFault();
        
        Matcher matcher = matches(UNDELIVERABLE, subject);
        if (matcher != null) {
            apply(matcher, fault, response);
            logger.error("Undeliverable email, \nsubject: [{}]", subject);
        }
    }
    
}
