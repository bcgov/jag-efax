package ca.bc.gov.ag.efax.ws.exception;

public class FAXSendFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public FAXSendFault() {
        super(FaultId.FAX_SEND_FAULT);
    }
    
    public FAXSendFault(String message) {
        super(FaultId.FAX_SEND_FAULT, message);
    }

    public FAXSendFault(String jobId, Throwable throwable) {
        super(FaultId.FAX_SEND_FAULT, jobId, throwable.toString());
    }

}
