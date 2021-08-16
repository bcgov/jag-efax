package ca.bc.gov.ag.efax.mail.service.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.ag.efax.mail.model.DocumentDistributionMainProcessProcessResponseDecorator;
import ca.bc.gov.ag.efax.mail.util.StringUtils;

public class SucceededVisitor extends EmailVisitor {

    private final static Logger logger = LoggerFactory.getLogger(SucceededVisitor.class);
    
    /** A regex pattern for the expected subject line of a fax that succeeded. */
    private static final String SUBJECT = "Message Succeeded: .*";
    private static final String BODY = ".*<jobId>(\\d+)</jobId><uuid>(.+)</uuid>.*";

    public static final String STATUS_CODE = "SUCC";
    public static final String STATUS_MESSAGE = "Successful";

    @Override
    public void apply(String subject, String body, DocumentDistributionMainProcessProcessResponseDecorator response) {
        if (Pattern.matches(SUBJECT, subject)) {
            String simplifiedBody = StringUtils.decodeString(body);
            
            Matcher matcher = matches(BODY, simplifiedBody);
            if (matcher != null) {
                apply(matcher, STATUS_CODE, STATUS_MESSAGE, response);
                logger.trace("Message Succeeded: jobId:{}", matcher.group(1));
            }
        }
    }
}
