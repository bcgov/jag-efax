package ca.bc.gov.ag.dist.ws.util;

import java.util.UUID;

import ca.bc.gov.ag.dist.mailservice.MailMessage;
import ca.bc.gov.ag.dist.ws.exception.FaxTransformationFault;
import ca.bc.gov.ag.dist.ws.exception.RuntimeFault;
import ca.bc.gov.ag.dist.ws.model.DocumentDistributionRequest;

public class FaxUtils {

    /**
     * Attempts to construct an email recipient that Exchange Server expects based on the faxFormat, to, and faxNumber.
     * 
     * <i>Content copied from DocumentDistributionMainProcess.bpel:constructFaxDestination</i>
     * 
     * @param jobId the id of this fax to process.
     * @param faxFormat a pattern Exchange Server expects to see when sending fax messages
     * @param to        the recipient's name in an email
     * @param faxNumber the recipient's fax number in an email
     * @return a formatted string Exchange Server expects to see to send faxes ie. IMCEAFAX-Somebody+402505555555@domain.com
     */
    public static String getFaxDestination(String faxFormat, String to, String faxNumber) {
        // FIXME: techdept - codeclimate reports this method is too complicated and large (complexity of 8, max 5 allowed)

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
            String newTo = "";
            for (int i = 0; i < to.length(); i++) {
                if (to.charAt(i) == ' ') {
                    newTo += "+20";
                } else {
                    newTo += to.charAt(i);
                }
            }

            // Normalize fax number - strip out all non-digits
            String newFaxNumber = "";
            for (int i = 0; i < faxNumber.length(); i++) {
                char c = faxNumber.charAt(i);
                if (Character.isDigit(c)) {
                    newFaxNumber += c;
                }
            }

            // Construct new fax destination
            String toPlaceholder = "%RECIPIENT%";
            String faxPlaceholder = "%FAXNUMBER%";

            int i1 = faxFormat.indexOf(toPlaceholder);
            int i2 = i1 + toPlaceholder.length();
            int i3 = faxFormat.indexOf(faxPlaceholder);
            int i4 = i3 + faxPlaceholder.length();

            String s1 = faxFormat.substring(0, i1);
            String s2 = faxFormat.substring(i2, i3);
            String s3 = faxFormat.substring(i4, faxFormat.length());

            return s1 + newTo + s2 + newFaxNumber + s3;
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
