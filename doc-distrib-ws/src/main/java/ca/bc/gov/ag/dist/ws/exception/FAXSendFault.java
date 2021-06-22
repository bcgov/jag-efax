package ca.bc.gov.ag.dist.ws.exception;

public class FAXSendFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public FAXSendFault(String jobId, String message) {
        super(FaultId.FAX_SEND_FAULT, jobId, message);
    }

}
