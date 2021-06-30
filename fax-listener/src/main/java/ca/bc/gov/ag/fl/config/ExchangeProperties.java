package ca.bc.gov.ag.fl.config;

import javax.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * This class represents the Exchange custom properties
 */
@Validated
@ConfigurationProperties(prefix = "exchange.service")
public class ExchangeProperties {

    @NotEmpty
    private String username;
    @NotEmpty
    private String password;
    @NotEmpty
    private String endpoint;

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

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
}
