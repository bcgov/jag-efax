package ca.bc.gov.ag.dist.ws.exception;

public class CatchAllFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public CatchAllFault(String jobId, String message) {
        super(FaultId.CATCH_ALL_FAULT, jobId, message);
    }

}
