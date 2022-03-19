package ca.bc.gov.ag.proxy.validation;

import ca.bc.gov.ag.proxy.DocumentDistributionRequestBuilder;

import static ca.bc.gov.ag.proxy.validation.IntegerValidationHelper.*;
import static ca.bc.gov.ag.proxy.validation.StringValidationHelper.notEmpty;
import static ca.bc.gov.ag.proxy.validation.StringValidationHelper.notNull;

public class DocumentDistributionRequestBuilderValidator {
    public static void validatePreBuild(DocumentDistributionRequestBuilder builder) {
        notNull.and(notEmpty).test(builder.getFrom()).throwIfInvalid("from");
        notNull.and(notEmpty).test(builder.getTo()).throwIfInvalid("to");
        notNull.and(notEmpty).test(builder.getJobId()).throwIfInvalid("jobId");
        notNull.and(notEmpty).test(builder.getStringDateTime()).throwIfInvalid("dateTime");
        notNull.and(notEmpty).test(builder.getTimeout().toString()).throwIfInvalid("timeout");
        notNull.and(notEmpty).test(builder.getChannel()).throwIfInvalid("schannel");
        notNull.and(notEmpty).test(builder.getTransport()).throwIfInvalid("transport");
        notNull.and(notEmpty).test(builder.getSubject()).throwIfInvalid("subject");

        greaterThanOrEqualTo(1)
                .test(builder.getAttachments().getUri().size())
                .throwIfInvalid("attachment");

        notNull.test(builder.getExtension1()).throwIfInvalid("extension1");
        notNull.test(builder.getExtension2()).throwIfInvalid("extension2");
        notNull.test(builder.getFromFaxNumber()).throwIfInvalid("fromFaxNumber()");
        notNull.test(builder.getFromPhoneNumber()).throwIfInvalid("fromPhoneNumber");
        notNull.test(builder.getFileNumber()).throwIfInvalid("fileNumber");
    }

}
