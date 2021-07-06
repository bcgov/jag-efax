package ca.bc.gov.ag.efax.mail.service.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionMainProcessProcessUpdate;
import ca.bc.gov.ag.efax.mail.util.DateUtil;
import ca.bc.gov.ag.efax.ws.exception.FAXListenFault;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

public class EmailParser {

    private final Logger logger = LoggerFactory.getLogger(EmailParser.class);

    private List<EmailVisitor> visitors = new ArrayList<EmailVisitor>();

    /** Registers (adds) an EmailVisitor to this parser. */
    public void registerVisitor(EmailVisitor visitor) {
        visitors.add(visitor);
    }

    /**
     * Attempts to parse the emailMessage, extracting the jobId, uuid, and status.
     * 
     * @param emailMessage
     * @return
     * @throws Exception
     */
    public DocumentDistributionMainProcessProcessUpdate parse(EmailMessage emailMessage) throws Exception {
        DocumentDistributionMainProcessProcessUpdate response = new DocumentDistributionMainProcessProcessUpdate();
        response.setDateTime(DateUtil.toXMLGregorianCalendar(emailMessage.getDateTimeReceived()));

        String subject = emailMessage.getSubject();
        String body = MessageBody.getStringFromMessageBody(emailMessage.getBody());

        // Try every registered email visitor to see if we can parse the email response from MS Exchange.
        for (EmailVisitor emailParserVisitor : visitors) {
            if (hasJobId(response))
                break;
            emailParserVisitor.apply(subject, body, response);
        }

        if (!hasJobId(response)) {
            // Could not parse email. Log the email as an error rather than throwing an exception so control can flow to the next email 
            // which may not have an issue.
            logger.error("Unrecognized email, \nsubject: [{}]", subject);
        }

        // Default error to FAXListenFault if a jobId was found, but could not parse a status.
        else if (!hasStatus(response)) {
            logger.error("Unrecognized email, could not find status, \nsubject: [{}]", subject);
            FAXListenFault faxListenFault = new FAXListenFault();
            response.setStatus(faxListenFault.getFaultCode());
            response.setStatus(faxListenFault.getFaultMessage());
        }

        return response;
    }

    private boolean hasJobId(DocumentDistributionMainProcessProcessUpdate response) {
        return !StringUtils.isEmpty(response.getJobId());
    }

    private boolean hasStatus(DocumentDistributionMainProcessProcessUpdate response) {
        return !StringUtils.isEmpty(response.getJobId());
    }

}
