package ca.bc.gov.ag.efax.ws.exception;

public class FAXTimeoutFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public FAXTimeoutFault() {
        super(FaultId.FAX_TIMEOUT_FAULT);
    }
    
    public FAXTimeoutFault(String jobId, String message) {
        super(FaultId.FAX_TIMEOUT_FAULT, jobId, message);
    }

}
