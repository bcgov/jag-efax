package ca.bc.gov.ag.efax.ws.exception;

public class FAXListenFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public FAXListenFault() {
        super(FaultId.FAX_LISTEN_FAULT);
    }
    
    public FAXListenFault(String jobId, String message) {
        super(FaultId.FAX_LISTEN_FAULT, jobId, message);
    }

}
