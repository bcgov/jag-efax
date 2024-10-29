package ca.bc.gov.ag.efax.ws.service;

import ca.bc.gov.ag.efax.mail.model.SentMessage;
import ca.bc.gov.ag.efax.ws.exception.FAXSendFault;
import ca.bc.gov.ag.efax.ws.exception.FaxTransformationFault;
import ca.bc.gov.ag.efax.ws.exception.UnknownChannelFault;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionMainProcessProcessResponse;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;
//import microsoft.exchange.webservices.data.core.service.item.EmailMessage;

public interface DocumentDistributionService {

    /**
     * Following the BPEL logic from the legacy application, this method will initiate the process to send a fax.
     * <p>
     * A successful request will result in an {@link EmailMessage} being sent to Microsoft Exchange and a {@link SentMessage} added to the sentMessage
     * redis queue. Microsoft will in turn process the email and convert it to a fax message sent to the faxNumber
     * (DocumentDistributionRequest#setTransport(String value))
     * </p>
     * <p>
     * An unsuccessful request will simply result in a runtime exception thrown - no message sent to exchange, no record added to redis.
     * </p>
     * 
     * @param request a value object wrapping the fax request to send.
     * @throws UnknownChannelFault if the request channel is not "fax" (only fax is implemented)
     * @throws FaxTransformationFault if the faxNumber (DocumentDistributionRequest#setTransport(String value)) could not be processed.
     * @throws FAXSendFault if there was an error using MS Exchange
     */
    public void initiateRequestToSendMessage(DocumentDistributionRequest request);

    /**
     * Sends the specified response object to Justin's SOAP callback, notifying the client of the jobId and status of the attempted fax.
     * <p>
     * As a callback routine, any errors are quietly logged and suppressed.
     * </p>
     * 
     * @param response a value object encapsulating the attempted fax response.
     */
    public void sendResponseToCallback(DocumentDistributionMainProcessProcessResponse response);

}
