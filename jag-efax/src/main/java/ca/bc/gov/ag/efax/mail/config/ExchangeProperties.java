package ca.bc.gov.ag.efax.mail.config;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "exchange.service")
public class ExchangeProperties {

	@NotEmpty
	private String endpoint;
	@NotEmpty
	private String username;
	@NotEmpty
	private String password;
	private boolean saveInSent;
	private String tempDirectory;

	public String getEndpoint() {
        return endpoint;
    }
	
	public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getSaveInSent() {
		return saveInSent;
	}

	public void setSaveInSent(boolean saveInSent) {
		this.saveInSent = saveInSent;
	}

	/**
	 * Returns the temporary directory ending with the OS file path separator (ie. \)
	 */
	public String getTempDirectory() {
	    String path = "";
		if (StringUtils.hasLength(tempDirectory))
		    path = tempDirectory;
		else
			path = System.getProperty("java.io.tmpdir");
		
        String separator = System.getProperty("file.separator");
		if (!path.endsWith(separator)) {
		    path += separator;
		}
		
		return path;
	}

	public void setTempDirectory(String tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

}
