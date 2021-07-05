package ca.bc.gov.ag.efax.mail.service.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionMainProcessProcessUpdate;
import ca.bc.gov.ag.efax.ws.exception.FAXListenFault;

public class UndeliverableVisitor implements EmailVisitor {

    private final static Logger logger = LoggerFactory.getLogger(UndeliverableVisitor.class);
    
    /** A regex pattern for the expected subject line of an undeliverable fax */
    private static final String UNDELIVERABLE = "Undeliverable: <jobId>(\\d+)</jobId><uuid>(.+)</uuid>";

    @Override
    public void apply(String subject, String body, DocumentDistributionMainProcessProcessUpdate response) {
        if (Pattern.matches(UNDELIVERABLE, subject)) {
            Matcher matcher = Pattern.compile(UNDELIVERABLE).matcher(subject);
            if (matcher.find()) {
                FAXListenFault fault = new FAXListenFault();
                response.setStatus(fault.getFaultCode());
                response.setStatusMsg(fault.getFaultMessage());
                response.setJobId(matcher.group(1));
                response.setUuid(matcher.group(2));
                logger.error("Undeliverable email, \nsubject: [{}], \nbody: [{}]", subject, body);
            }
        }
    }
}
