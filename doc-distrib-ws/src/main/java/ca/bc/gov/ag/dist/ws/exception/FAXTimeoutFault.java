package ca.bc.gov.ag.dist.ws.exception;

public class FAXTimeoutFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public FAXTimeoutFault(String jobId, String message) {
        super(FaultId.FAX_TIMEOUT_FAULT, jobId, message);
    }

}
