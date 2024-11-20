package ca.bc.gov.ag.efax.ws.exception;

public class ServiceFault {
	
	private String code;
	private String message;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static ServiceFault build(String code, String message) {
		ServiceFault serviceFault = new ServiceFault();
		serviceFault.setCode(code);
		serviceFault.setMessage(message);
		return serviceFault;
	}
}
