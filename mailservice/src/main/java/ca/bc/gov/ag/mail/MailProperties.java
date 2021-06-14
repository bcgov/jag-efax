package ca.bc.gov.ag.mail;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "mailservice")
public class MailProperties {

	@NotEmpty
	private String exchangeServer;
	@NotEmpty
	private String mailAccountAlias;
	@NotEmpty
	private String exchangeWSEndpoint;
	@NotEmpty
	private String username;
	@NotEmpty
	private String password;
	@NotEmpty
	private String folderPath;
	private boolean saveInSent;
	private String tempDirectory;

	public String getExchangeServer() {
		return exchangeServer;
	}

	public void setExchangeServer(String exchangeServer) {
		this.exchangeServer = exchangeServer;
	}

	public String getMailAccountAlias() {
		return mailAccountAlias;
	}

	public void setMailAccountAlias(String mailAccountAlias) {
		this.mailAccountAlias = mailAccountAlias;
	}

	public String getExchangeWSEndpoint() {
		return exchangeWSEndpoint;
	}

	public void setExchangeWSEndpoint(String exchangeWSEndpoint) {
		this.exchangeWSEndpoint = exchangeWSEndpoint;
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

	public String getFolderPath() {
		return folderPath;
	}

	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}

	public boolean getSaveInSent() {
		return saveInSent;
	}

	public void setSaveInSent(boolean saveInSent) {
		this.saveInSent = saveInSent;
	}

	public String getTempDirectory() {
		if (StringUtils.hasLength(tempDirectory))
			return tempDirectory;
		else
			return System.getProperty("java.io.tmpdir");
	}

	public void setTempDirectory(String tempDirectory) {
		this.tempDirectory = tempDirectory;
	}

}
