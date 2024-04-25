package ca.bc.gov.ag.efax.ws.config;

import java.util.Map;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import ca.bc.gov.ag.efax.ws.exception.ServiceFault;

@Validated
@ConfigurationProperties(prefix = "ws")
public class DocumentDistributionProperties {

    @NotEmpty
    @Pattern(regexp = "^.*%FAXNUMBER%@.*$")
    private String faxFormat;

    private JustinCallbackProperties callback;

    @NotEmpty
    private Map<Integer, ServiceFault> faults;

    public String getFaxFormat() {
        return faxFormat;
    }

    public void setFaxFormat(String faxFormat) {
        this.faxFormat = faxFormat;
    }

    public JustinCallbackProperties getCallback() {
        return callback;
    }

    public void setCallback(JustinCallbackProperties callback) {
        this.callback = callback;
    }

    public Map<Integer, ServiceFault> getFaults() {
        return faults;
    }

    public void setFaults(Map<Integer, ServiceFault> faults) {
        this.faults = faults;
    }
}
