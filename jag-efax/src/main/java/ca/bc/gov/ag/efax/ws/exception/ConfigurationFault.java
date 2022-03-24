package ca.bc.gov.ag.efax.ws.exception;

public class ConfigurationFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public ConfigurationFault(String jobId, String message) {
        super(FaultId.CONFIGURATION_FAULT, jobId, message);
    }

}
