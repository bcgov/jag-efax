package ca.bc.gov.ag.efax.ws.exception;

public class CatchAllFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public CatchAllFault(String message) {
        super(FaultId.CATCH_ALL_FAULT, message);
    }

    public CatchAllFault(String jobId, String message) {
        super(FaultId.CATCH_ALL_FAULT, jobId, message);
    }

    public CatchAllFault(String jobId, Throwable throwable) {
        super(FaultId.CATCH_ALL_FAULT, jobId, throwable.toString());
    }

}
