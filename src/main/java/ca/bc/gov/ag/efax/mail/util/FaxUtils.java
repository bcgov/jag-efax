package ca.bc.gov.ag.efax.mail.util;

import java.util.UUID;

import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionRequest;
import ca.bc.gov.ag.efax.mail.model.MailMessage;
import ca.bc.gov.ag.efax.ws.exception.FaxTransformationFault;
import ca.bc.gov.ag.efax.ws.exception.RuntimeFault;

public class FaxUtils {

    public static String PLACEHOLDER_RECIPIENT = "%RECIPIENT%";
    public static String PLACEHOLDER_FAXNUMBER = "%FAXNUMBER%";
    
    /**
     * Attempts to construct an email recipient that Exchange Server expects based on the faxFormat, to, and faxNumber.
     * 
     * <i>Content copied (and simplified) from DocumentDistributionMainProcess.bpel:constructFaxDestination</i>
     * 
     * @param jobId the id of this fax to process.
     * @param faxFormat a pattern Exchange Server expects to see when sending fax messages
     * @param to        the recipient's name in an email
     * @param faxNumber the recipient's fax number in an email
     * @return a formatted string Exchange Server expects to see to send faxes ie. IMCEAFAX-Somebody+402505555555@domain.com
     */
    public static String getFaxDestination(String faxFormat, String to, String faxNumber) {
        try {
            // Left in for testing - if fax number contains an '@', it is
            // likely an email address so just pass through the fax number unchanged.
            if (faxNumber.contains("@")) {
                return faxNumber;
            }

            // Normalize - replace invalid characters in 'to' param
            String replacable = "[()\"<>:;%,+@]"; // regular expression
            to = to.replaceAll(replacable, "_");

            // Normalize to - set spaces to %20
            String newTo = to.replaceAll(" ", "+20");

            // Normalize fax number - strip out all non-digits
            String newFaxNumber = faxNumber.replaceAll("\\D", "");

            String recipient = faxFormat.replace(PLACEHOLDER_RECIPIENT, newTo);
            recipient = recipient.replace(PLACEHOLDER_FAXNUMBER, newFaxNumber);

            return recipient;
        } catch (Exception e) {
            throw new FaxTransformationFault("Could not format fax destination");
        }
    }

    /**
     * Copies and maps request parameters to MailMessage properties. <i>Based on the logic found in
     * DocumentDistributionMainProcess.bpel:prepareFaxMessage and SendFaxService/bpel/transformSendMessage.xsl</i>
     */
    public static MailMessage prepareFaxMessage(UUID uuid, DocumentDistributionRequest request) {
        try {
            StringBuffer subject = new StringBuffer();
            subject.append("<jobId>");
            subject.append(request.getJobId());
            subject.append("</jobId>");
            subject.append("<uuid>");
            subject.append(uuid.toString());
            subject.append("</uuid>");
    
            MailMessage mailMessage = new MailMessage();
            mailMessage.setUuid(uuid.toString());
            mailMessage.setJobId(request.getJobId());
            mailMessage.setTo(request.getTransport());
            mailMessage.setSubject(subject.toString());
            mailMessage.setBody(request.getBody());
            mailMessage.setAttachments(request.getAttachments().getUri());
            return mailMessage;        
        } catch (Exception e) {
            throw new RuntimeFault(e.getMessage());
        }
    }

}
