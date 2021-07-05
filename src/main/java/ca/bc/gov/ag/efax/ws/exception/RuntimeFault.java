package ca.bc.gov.ag.efax.ws.exception;

public class RuntimeFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public RuntimeFault(String message) {
        super(FaultId.RUNTIME_FAULT, message);
    }
    
    public RuntimeFault(String jobId, String message) {
        super(FaultId.RUNTIME_FAULT, jobId, message);
    }

    public RuntimeFault(String jobId, Throwable throwable) {
        super(FaultId.RUNTIME_FAULT, jobId, throwable.toString());
    }

}
