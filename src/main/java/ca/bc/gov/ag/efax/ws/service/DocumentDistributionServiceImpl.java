package ca.bc.gov.ag.efax.ws.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionMainProcessProcessUpdate;
import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionRequest;
import ca.bc.gov.ag.efax.mail.model.MailMessage;
import ca.bc.gov.ag.efax.mail.model.RequestChannel;
import ca.bc.gov.ag.efax.mail.service.EmailService;
import ca.bc.gov.ag.efax.mail.util.FaxUtils;
import ca.bc.gov.ag.efax.ws.config.DocumentDistributionProperties;
import ca.bc.gov.ag.efax.ws.exception.UnknownChannelFault;

@Service
public class DocumentDistributionServiceImpl implements DocumentDistributionService {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private DocumentDistributionProperties documentDistributionProperties;

    @Autowired
    private EmailService emailService;

    @Override
    public void receiveRequestToSendMessage(DocumentDistributionRequest request) {
        if (!RequestChannel.FAX.getName().equals(request.getChannel())) {
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
    public void sendResponseToCallback(DocumentDistributionMainProcessProcessUpdate response) {
        if (documentDistributionProperties.getCallback().isEnabled()) {
            logger.trace("Justin Callback: jobId:{}, status:{}, msg:{}", response.getJobId(), response.getStatus(), response.getStatusMsg());
            // TODO: implement
        }
    }    
    
}
