package ca.bc.gov.ag.dist.ws.exception;

public class FaxTransformationFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public FaxTransformationFault(String message) {
        super(FaultId.FAX_TRANSFORM_FAULT, message);
    }

    public FaxTransformationFault(String jobId, String message) {
        super(FaultId.FAX_TRANSFORM_FAULT, jobId, message);
    }

    public FaxTransformationFault(String jobId, Throwable throwable) {
        super(FaultId.FAX_TRANSFORM_FAULT, jobId, throwable.toString());
    }

}
