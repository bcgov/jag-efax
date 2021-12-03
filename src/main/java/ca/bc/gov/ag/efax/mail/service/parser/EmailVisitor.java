package ca.bc.gov.ag.efax.mail.service.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.bc.gov.ag.efax.mail.model.DocumentDistributionMainProcessProcessResponseDecorator;
import ca.bc.gov.ag.efax.mail.util.StringUtils;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;

public abstract class EmailVisitor {

    public abstract void apply(String subject, String body, DocumentDistributionMainProcessProcessResponseDecorator response);

    protected Matcher matches(String pattern, String string) {
        if (Pattern.matches(pattern, string)) {
            Matcher matcher = Pattern.compile(pattern).matcher(string);
            if (matcher.find()) {
                return matcher;
            }
        }
        return null;
    }

    /**
     * Applies the matcher results and fault code/message to the provided response.
     */
    protected void apply(Matcher matcher, FAXSendFault fault, DocumentDistributionMainProcessProcessResponseDecorator response) {
        response.setJobId(matcher.group(1));
        // There shouldn't be any whitespace in the uuid.  This uuid was extracted from the subject line in an Exchange email.  
        // It seems Exchange will sometimes inject spaces into the uuid string 
        response.setUuid(StringUtils.removeWhiteSpace(matcher.group(2)));
        response.setStatusCode(fault.getFaultCode());
        response.setStatusMessage(fault.getFaultMessage());
    }

    /**
     * Applies the matcher results and given code/message to the provided response.
     */
    protected void apply(Matcher matcher, String statusCode, String statusMessage, DocumentDistributionMainProcessProcessResponseDecorator response) {
        response.setJobId(matcher.group(1));
        // There shouldn't be any whitespace in the uuid.  This uuid was extracted from the subject line in an Exchange email.  
        // It seems Exchange will sometimes inject spaces into the uuid string 
        response.setUuid(StringUtils.removeWhiteSpace(matcher.group(2)));
        response.setStatusCode(statusCode);
        response.setStatusMessage(statusMessage);
    }
    
}
