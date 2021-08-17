package ca.bc.gov.ag.efax.mail.service.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.ag.efax.mail.model.DocumentDistributionMainProcessProcessResponseDecorator;
import ca.bc.gov.ag.efax.mail.util.StringUtils;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;

public class FailedVisitor extends EmailVisitor {

    private final static Logger logger = LoggerFactory.getLogger(FailedVisitor.class);
    
    /** A regex pattern for the expected subject line of a failed fax */
    private static final String SUBJECT = "Message Failed: .*";
    private static final String BODY = ".*<jobId>(\\d+)</jobId><uuid>(.+)</uuid>.*";

    @Override
    public void apply(String subject, String body, DocumentDistributionMainProcessProcessResponseDecorator response) {
        if (Pattern.matches(SUBJECT, subject)) {
            String simplifiedBody = StringUtils.decodeString(body);
            FAXSendFault fault = new FAXSendFault();            
            
            Matcher matcher = matches(BODY, simplifiedBody);
            if (matcher != null) {
                apply(matcher, fault, response);
                logger.error("Undeliverable email, \nsubject: [{}]", subject);
            }
        }
    }
}
