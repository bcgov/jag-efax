package ca.bc.gov.ag.dist.ws.exception;

public class RuntimeFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public RuntimeFault(String jobId, String message) {
        super(FaultId.RUNTIME_FAULT, jobId, message);
    }

}
