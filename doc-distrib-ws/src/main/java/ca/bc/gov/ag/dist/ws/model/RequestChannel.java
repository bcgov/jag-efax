package ca.bc.gov.ag.dist.ws.model;

public enum RequestChannel {

	FAX("fax"), EMAIL("email"), SMS("sms");
    
    private String name;
    
    private RequestChannel(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
	
}
