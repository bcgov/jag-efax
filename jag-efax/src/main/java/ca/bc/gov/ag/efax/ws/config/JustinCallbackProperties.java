package ca.bc.gov.ag.efax.ws.config;

public class JustinCallbackProperties {

    private boolean enabled = false;
    private String endpoint = null;

    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }
    
}
