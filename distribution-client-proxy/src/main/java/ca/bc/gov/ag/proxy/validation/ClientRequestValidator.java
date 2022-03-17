package ca.bc.gov.ag.proxy.validation;

import ca.bc.gov.ag.proxy.ClientRequest;

import static ca.bc.gov.ag.proxy.validation.StringValidationHelper.notEmpty;
import static ca.bc.gov.ag.proxy.validation.StringValidationHelper.notNull;

public class ClientRequestValidator {
    public static void validate(ClientRequest clientRequest){
        notNull.and(notEmpty).test(clientRequest.getFrom()).throwIfInvalid("from");
        notNull.and(notEmpty).test(clientRequest.getTo()).throwIfInvalid("to");
        notNull.and(notEmpty).test(clientRequest.getJobId()).throwIfInvalid("jobId");
        notNull.and(notEmpty).test(clientRequest.getSdateTime()).throwIfInvalid("sdateTime");
        notNull.and(notEmpty).test(clientRequest.getTimeout()).throwIfInvalid("timeout");
        notNull.and(notEmpty).test(clientRequest.getSchannel()).throwIfInvalid("schannel");
        notNull.and(notEmpty).test(clientRequest.getTransport()).throwIfInvalid("transport");
        notNull.and(notEmpty).test(clientRequest.getSubject()).throwIfInvalid("subject");
        notNull.and(notEmpty).test(clientRequest.getFileNumber()).throwIfInvalid("fileNumber");
        notNull.and(notEmpty).test(clientRequest.getSnumPages()).throwIfInvalid("snumPages");
        notNull.and(notEmpty).test(clientRequest.getAttachment()).throwIfInvalid("attachment");
        notNull.and(notEmpty).test(clientRequest.getExtension1()).throwIfInvalid("extension1");
        notNull.and(notEmpty).test(clientRequest.getExtension2()).throwIfInvalid("extension2");
        notNull.and(notEmpty).test(clientRequest.getFromFaxNumber()).throwIfInvalid("fromFaxNumber");
        notNull.and(notEmpty).test(clientRequest.getFromPhoneNumber()).throwIfInvalid("fromPhoneNumber");
    }
}
