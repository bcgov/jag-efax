package ca.bc.gov.ag.efax.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties()
public class AppProperties {
	
	@Value("${app.version}")
	private String appVersion;

	public String getAppVersion() {
		return appVersion;
	}
}


