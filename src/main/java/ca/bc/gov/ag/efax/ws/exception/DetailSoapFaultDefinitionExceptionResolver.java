package ca.bc.gov.ag.efax.ws.exception;

import javax.xml.namespace.QName;

import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

import ca.bc.gov.ag.efax.ws.model.DocumentDistributionMainProcessProcessFault;

public class DetailSoapFaultDefinitionExceptionResolver extends SoapFaultMappingExceptionResolver {

	private static final QName JOBID = new QName("jobId");
	private static final QName CODE = new QName("statusCode");
	private static final QName MESSAGE = new QName("message");

	@Override
	protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
		logger.warn("Exception processed ", ex);
		if (ex instanceof ServiceFaultException) {
			DocumentDistributionMainProcessProcessFault processFault = ((ServiceFaultException) ex).getProcessFault();
			SoapFaultDetail detail = fault.addFaultDetail();
			detail.addFaultDetailElement(JOBID).addText(processFault.getJobId());
			detail.addFaultDetailElement(CODE).addText(processFault.getFaultCode());
			detail.addFaultDetailElement(MESSAGE).addText(processFault.getFaultMessage());
		}
	}

}
