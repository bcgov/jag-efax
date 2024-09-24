package ca.bc.gov.ag.efax.graph.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties()
public class MSGraphProperties {
	
	@Value("${ms.graph.clientId}")
	private String clientId;
	
	@Value("${ms.graph.authority}")
	private String authority;
	
	@Value("${ms.graph.secretKey}")
	private String secretKey;
	
	@Value("${ms.graph.endpoint}")
	private String endpoint;
	
	@Value("${ms.graph.email.account}")
	private String emailAccount; 
	
	// Number of days before MS Graph API Secret Key Expiry Date to start sending notifications. 
	@Value("${ms.graph.expiry.threshold}")
	private String expiryThreshold; 
	
	@Value("${ms.graph.saveToSent}")
	private boolean saveToSent; 
	
	@Value("${ms.graph.tempDirectory}")
	private String tempDirectory;
	
	@Value("${ms.graph.adminEmail}")
	private String adminEmail; 
	
	@Value("${ms.graph.azureAppName}")
	private String azureAppName;
	

	public String getClientId() {
		return clientId;
	}

	public String getAuthority() {
		return authority;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getEmailAccount() {
		return emailAccount;
	}

	public String getExpiryThreshold() {
		return expiryThreshold;
	}

	public boolean isSaveToSent() {
		return saveToSent;
	}

	public String getTempDirectory() {
		return tempDirectory;
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public String getAzureAppName() {
		return azureAppName;
	}
	
}