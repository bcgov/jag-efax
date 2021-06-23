package ca.bc.gov.ag.dist.ws.exception;

public class FAXListenFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public FAXListenFault(String jobId, String message) {
        super(FaultId.FAX_LISTEN_FAULT, jobId, message);
    }

}
