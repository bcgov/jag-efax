package ca.bc.gov.ag.dist.ws.config;

import java.util.Map;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import ca.bc.gov.ag.dist.ws.exception.ServiceFault;

@Validated
@ConfigurationProperties(prefix = "ws")
public class DocumentDistributionProperties {

	@NotEmpty
	@Pattern(regexp = "^.*%RECIPIENT%.*%FAXNUMBER%@.*$")
	private String faxFormat;

	@NotEmpty
	private Map<Integer, ServiceFault> faults;

	public String getFaxFormat() {
		return faxFormat;
	}

	public void setFaxFormat(String faxFormat) {
		this.faxFormat = faxFormat;
	}

	public Map<Integer, ServiceFault> getFaults() {
		return faults;
	}

	public void setFaults(Map<Integer, ServiceFault> faults) {
		this.faults = faults;
	}
}
