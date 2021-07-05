package ca.bc.gov.ag.efax.ws.exception;

import ca.bc.gov.ag.dist.efax.ws.model.DocumentDistributionMainProcessProcessFault;
import ca.bc.gov.ag.efax.ws.config.DocumentDistributionProperties;
import ca.bc.gov.ag.efax.ws.config.SpringContext;

public abstract class ServiceFaultException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private DocumentDistributionProperties documentDistributionProperties;
    private String jobId;
    private FaultId faultId;
    
    public ServiceFaultException(FaultId faultId, String jobId, String message) {
        super(message);
        this.faultId = faultId;
        this.jobId = jobId;
        documentDistributionProperties = SpringContext.getBean(DocumentDistributionProperties.class);
    }

    public ServiceFaultException(FaultId faultId, String message) {
        this(faultId, null, message);
    }

    public DocumentDistributionMainProcessProcessFault getProcessFault() {
        ServiceFault serviceFault = documentDistributionProperties.getFaults().get(faultId.getId());

        DocumentDistributionMainProcessProcessFault fault = new DocumentDistributionMainProcessProcessFault();
        fault.setJobId(jobId);
        fault.setFaultCode(serviceFault.getCode());
        fault.setFaultMessage(serviceFault.getMessage());
        return fault;
    }

    protected enum FaultId {

        CONFIGURATION_FAULT(Integer.valueOf(100)), 
        FAX_SEND_FAULT(Integer.valueOf(200)),
        FAX_LISTEN_FAULT(Integer.valueOf(201)), 
        FAX_TIMEOUT_FAULT(Integer.valueOf(202)),
        FAX_UPDATE_FAULT(Integer.valueOf(203)), 
        FAX_TRANSFORM_FAULT(Integer.valueOf(204)),
        ON_RESULT_FAULT(Integer.valueOf(400)), 
        UNKNOWN_CHANNEL_FAULT(Integer.valueOf(500)),
        RUNTIME_FAULT(Integer.valueOf(600)), 
        CATCH_ALL_FAULT(Integer.valueOf(700));

        private Integer id;

        private FaultId(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }
    }

}
