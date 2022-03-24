package ca.bc.gov.ag.efax.ws.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import ca.bc.gov.ag.efax.mail.model.MailMessage;
import ca.bc.gov.ag.efax.mail.model.RequestChannel;
import ca.bc.gov.ag.efax.mail.service.EmailService;
import ca.bc.gov.ag.efax.mail.util.FaxUtils;
import ca.bc.gov.ag.efax.mail.util.StringUtils;
import ca.bc.gov.ag.efax.ws.config.DocumentDistributionProperties;
import ca.bc.gov.ag.efax.ws.exception.UnknownChannelFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionMainProcessProcessResponse;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;

@Service
public class DocumentDistributionServiceImpl extends WebServiceGatewaySupport implements DocumentDistributionService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private DocumentDistributionProperties documentDistributionProperties;

    @Autowired
    private EmailService emailService;

    public DocumentDistributionServiceImpl(DocumentDistributionProperties documentDistributionProperties, Jaxb2Marshaller marshaller) {
        setDefaultUri(documentDistributionProperties.getCallback().getEndpoint());
        setMarshaller(marshaller);
        setUnmarshaller(marshaller);
    }
    
    @Override
    public void initiateRequestToSendMessage(DocumentDistributionRequest request) {
        if (!RequestChannel.FAX.getName().equalsIgnoreCase(request.getChannel())) {
            throw new UnknownChannelFault(request.getJobId(), "Unrecognized channel - only 'fax' is implemented.");
        }
        
        // DocumentDistributionMainProcess.bpel:generateInternalUUID
        UUID uuid = UUID.randomUUID();

        // DocumentDistributionMainProcess.bpel:constructFaxDestination
        String faxDestination = FaxUtils.getFaxDestination(documentDistributionProperties.getFaxFormat(), request.getTo(), request.getTransport());

        // DocumentDistributionMainProcess.bpel:assignFinalFaxNumber
        request.setTransport(faxDestination);

        // DocumentDistributionMainProcess.bpel:prepareFaxMessage
        // DocumentDistributionMainProcess.bpel:getAttachmentCount
        // DocumentDistributionMainProcess.bpel:addAttachment
        // DocumentDistributionMainProcess.bpel:setDocSentDateTimeStamp
        MailMessage mailMessage = FaxUtils.prepareFaxMessage(uuid, request);

        // DocumentDistributionMainProcess.bpel:sendFax-Synch
        emailService.sendMessage(mailMessage);
    }

    @Override
    public void sendResponseToCallback(DocumentDistributionMainProcessProcessResponse response) {
        if (documentDistributionProperties.getCallback().isEnabled()) {
            try {
                logger.info("Justin callback: attempting to send response:{}", StringUtils.toXMLString(response));
                getWebServiceTemplate().marshalSendAndReceive(response);
                logger.info("Justin callback: successfully sent response:{}", StringUtils.toXMLString(response));
            }
            catch (Exception e) {
                logger.error("Justin callback error, failed to send response: ", e);
            }
        }
        else {
            logger.trace("Justin callback skipped: disabled.");            
        }
    }    
    
}
