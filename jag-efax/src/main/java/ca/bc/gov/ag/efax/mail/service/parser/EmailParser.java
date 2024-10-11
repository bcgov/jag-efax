package ca.bc.gov.ag.efax.mail.service.parser;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.bc.gov.ag.efax.graph.model.GmailMessage;
import ca.bc.gov.ag.efax.mail.model.DocumentDistributionMainProcessProcessResponseDecorator;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionMainProcessProcessResponse;

@Service
public class EmailParser {

    private final Logger logger = LoggerFactory.getLogger(EmailParser.class);

    private List<EmailVisitor> visitors = new ArrayList<EmailVisitor>();
    
    @PostConstruct
    private void postConstruct() {
    	this.registerVisitor(new SucceededVisitor());
    	this.registerVisitor(new UndeliverableVisitor());
    	this.registerVisitor(new FailedVisitor());
    }
    
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
    public DocumentDistributionMainProcessProcessResponseDecorator parse(GmailMessage emailMessage) throws Exception {	
    
    	DocumentDistributionMainProcessProcessResponseDecorator response = new DocumentDistributionMainProcessProcessResponseDecorator();

        String subject = emailMessage.getSubject();
        
        // Body should be in clear text at this stage (no HTML formatting included). 
        String body = emailMessage.getBody();

        // Try every registered email visitor to see if we can parse the email response from MS Graph API.
        for (EmailVisitor emailParserVisitor : visitors) {
            if (hasJobId(response))
                break;
            emailParserVisitor.apply(subject, body, response);
        }

        if (!hasJobId(response)) {
            // Could not parse email (could not find jobId). Log the email as an error rather than throwing an exception so 
            // control can flow to the next email which may not have an issue.
            logger.error("Unrecognized email, \nsubject: [{}]", subject);
        }

        // Default error to FAXSendFault if a jobId was found, but could not parse a status.
        else if (!hasStatus(response)) {
            logger.error("Unrecognized email, could not find status, \nsubject: [{}]", subject);
            FAXSendFault faxListenFault = new FAXSendFault();
            response.setStatusCode(faxListenFault.getFaultCode());
            response.setStatusMessage(faxListenFault.getFaultMessage());
        }

        return response;
    }

    private boolean hasJobId(DocumentDistributionMainProcessProcessResponse response) {
        return !StringUtils.isEmpty(response.getJobId());
    }

    private boolean hasStatus(DocumentDistributionMainProcessProcessResponse response) {
        return !StringUtils.isEmpty(response.getStatusCode());
    }

}
