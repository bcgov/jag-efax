package ca.bc.gov.ag.dist.ws.exception;

public class FaxTransformationFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public FaxTransformationFault(String jobId, String message) {
        super(FaultId.FAX_TRANSFORM_FAULT, jobId, message);
    }

}
