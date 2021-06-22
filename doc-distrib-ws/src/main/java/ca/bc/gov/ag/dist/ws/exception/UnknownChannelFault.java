package ca.bc.gov.ag.dist.ws.exception;

public class UnknownChannelFault extends ServiceFaultException {

    private static final long serialVersionUID = 1L;

    public UnknownChannelFault(String jobId, String message) {
        super(FaultId.UNKNOWN_CHANNEL_FAULT, jobId, message);
    }

}
