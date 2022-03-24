package ca.bc.gov.ag.efax.mail.model;

import ca.bc.gov.ag.efax.ws.model.DocumentDistributionMainProcessProcessResponse;

/** A wrapper class of the generated DocumentDistributionMainProcessProcessResponse object. */
public class DocumentDistributionMainProcessProcessResponseDecorator extends DocumentDistributionMainProcessProcessResponse {

    private String uuid;
    
    public String getUuid() {
        return uuid;
    }
    
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

}
