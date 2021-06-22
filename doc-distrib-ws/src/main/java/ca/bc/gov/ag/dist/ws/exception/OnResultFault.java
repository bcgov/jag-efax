package ca.bc.gov.ag.dist.ws.exception;

public class OnResultFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public OnResultFault(String jobId, String message) {
        super(FaultId.ON_RESULT_FAULT, jobId, message);
    }

}
