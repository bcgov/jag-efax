package ca.bc.gov.ag.efax.ws.service;

import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionMainProcessProcessUpdate;
import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionRequest;

public interface DocumentDistributionService {

    /**
     * Following the BPEL logic from the legacy application, this method will process a request to send a fax message.
     * 
     * @param request a value object wrapping the fax request to send.
     */
    public void receiveRequestToSendMessage(DocumentDistributionRequest request);

    /**
     * Sends the specified response object to Justin's SOAP callback, notifying the client of the jobId and status of the attempted fax.
     * 
     * @param response a value object encapsulating the attempted fax response.
     */
    public void sendResponseToCallback(DocumentDistributionMainProcessProcessUpdate response);

}
