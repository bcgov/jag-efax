package ca.bc.gov.ag.efax.ws.exception;

public class FAXUpdateFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public FAXUpdateFault(String jobId, String message) {
        super(FaultId.FAX_UPDATE_FAULT, jobId, message);
    }

}
