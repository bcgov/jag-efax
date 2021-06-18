package ca.bc.gov.ag.dist.ws.config;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "ws")
public class DocumentDistributionProperties {

	@NotEmpty
	@Pattern(regexp = "^.*%RECIPIENT%.*%FAXNUMBER%@.*$")
	private String faxFormat;
	@NotEmpty
	private String configurationFaultId;
	@NotEmpty
	private String configurationFaultCode;
	@NotEmpty
	private String configurationFaultMessage;
	@NotEmpty
	private String faxSendFaultId;
	@NotEmpty
	private String faxSendFaultCode;
	@NotEmpty
	private String faxSendFaultMessage;
	@NotEmpty
	private String faxListenFaultId;
	@NotEmpty
	private String faxListenFaultCode;
	@NotEmpty
	private String faxListenFaultMessage;
	@NotEmpty
	private String faxTimeoutFaultId;
	@NotEmpty
	private String faxTimeoutFaultCode;
	@NotEmpty
	private String faxTimeoutFaultMessage;
	@NotEmpty
	private String faxUpdateFaultId;
	@NotEmpty
	private String faxUpdateFaultCode;
	@NotEmpty
	private String faxUpdateFaultMessage;
	@NotEmpty
	private String faxTransformFaultId;
	@NotEmpty
	private String faxTransformFaultCode;
	@NotEmpty
	private String faxTransformFaultMessage;
	@NotEmpty
	private String emailSendFaultId;
	@NotEmpty
	private String emailSendFaultCode;
	@NotEmpty
	private String emailSendFaultMessage;
	@NotEmpty
	private String emailListenFaultId;
	@NotEmpty
	private String emailListenFaultCode;
	@NotEmpty
	private String emailListenFaultMessage;
	@NotEmpty
	private String emailTimeoutFaultId;
	@NotEmpty
	private String emailTimeoutFaultCode;
	@NotEmpty
	private String emailTimeoutFaultMessage;
	@NotEmpty
	private String emailUpdateFaultId;
	@NotEmpty
	private String emailUpdateFaultCode;
	@NotEmpty
	private String emailUpdateFaultMessage;
	@NotEmpty
	private String onResultFaultId;
	@NotEmpty
	private String onResultFaultCode;
	@NotEmpty
	private String onResultFaultMessage;
	@NotEmpty
	private String unknownChannelFaultId;
	@NotEmpty
	private String unknownChannelFaultCode;
	@NotEmpty
	private String unknownChannelFaultMessage;
	@NotEmpty
	private String runtimeFaultId;
	@NotEmpty
	private String runtimeFaultCode;
	@NotEmpty
	private String runtimeFaultMessage;
	@NotEmpty
	private String catchAllFaultId;
	@NotEmpty
	private String catchAllFaultCode;
	@NotEmpty
	private String catchAllFaultMessage;

	public String getFaxFormat() {
		return faxFormat;
	}

	public void setFaxFormat(String faxFormat) {
		this.faxFormat = faxFormat;
	}

	public String getConfigurationFaultId() {
		return configurationFaultId;
	}

	public void setConfigurationFaultId(String configurationFaultId) {
		this.configurationFaultId = configurationFaultId;
	}

	public String getConfigurationFaultCode() {
		return configurationFaultCode;
	}

	public void setConfigurationFaultCode(String configurationFaultCode) {
		this.configurationFaultCode = configurationFaultCode;
	}

	public String getConfigurationFaultMessage() {
		return configurationFaultMessage;
	}

	public void setConfigurationFaultMessage(String configurationFaultMessage) {
		this.configurationFaultMessage = configurationFaultMessage;
	}

	public String getFaxSendFaultId() {
		return faxSendFaultId;
	}

	public void setFaxSendFaultId(String faxSendFaultId) {
		this.faxSendFaultId = faxSendFaultId;
	}

	public String getFaxSendFaultCode() {
		return faxSendFaultCode;
	}

	public void setFaxSendFaultCode(String faxSendFaultCode) {
		this.faxSendFaultCode = faxSendFaultCode;
	}

	public String getFaxSendFaultMessage() {
		return faxSendFaultMessage;
	}

	public void setFaxSendFaultMessage(String faxSendFaultMessage) {
		this.faxSendFaultMessage = faxSendFaultMessage;
	}

	public String getFaxListenFaultId() {
		return faxListenFaultId;
	}

	public void setFaxListenFaultId(String faxListenFaultId) {
		this.faxListenFaultId = faxListenFaultId;
	}

	public String getFaxListenFaultCode() {
		return faxListenFaultCode;
	}

	public void setFaxListenFaultCode(String faxListenFaultCode) {
		this.faxListenFaultCode = faxListenFaultCode;
	}

	public String getFaxListenFaultMessage() {
		return faxListenFaultMessage;
	}

	public void setFaxListenFaultMessage(String faxListenFaultMessage) {
		this.faxListenFaultMessage = faxListenFaultMessage;
	}

	public String getFaxTimeoutFaultId() {
		return faxTimeoutFaultId;
	}

	public void setFaxTimeoutFaultId(String faxTimeoutFaultId) {
		this.faxTimeoutFaultId = faxTimeoutFaultId;
	}

	public String getFaxTimeoutFaultCode() {
		return faxTimeoutFaultCode;
	}

	public void setFaxTimeoutFaultCode(String faxTimeoutFaultCode) {
		this.faxTimeoutFaultCode = faxTimeoutFaultCode;
	}

	public String getFaxTimeoutFaultMessage() {
		return faxTimeoutFaultMessage;
	}

	public void setFaxTimeoutFaultMessage(String faxTimeoutFaultMessage) {
		this.faxTimeoutFaultMessage = faxTimeoutFaultMessage;
	}

	public String getFaxUpdateFaultId() {
		return faxUpdateFaultId;
	}

	public void setFaxUpdateFaultId(String faxUpdateFaultId) {
		this.faxUpdateFaultId = faxUpdateFaultId;
	}

	public String getFaxUpdateFaultCode() {
		return faxUpdateFaultCode;
	}

	public void setFaxUpdateFaultCode(String faxUpdateFaultCode) {
		this.faxUpdateFaultCode = faxUpdateFaultCode;
	}

	public String getFaxUpdateFaultMessage() {
		return faxUpdateFaultMessage;
	}

	public void setFaxUpdateFaultMessage(String faxUpdateFaultMessage) {
		this.faxUpdateFaultMessage = faxUpdateFaultMessage;
	}

	public String getFaxTransformFaultId() {
		return faxTransformFaultId;
	}

	public void setFaxTransformFaultId(String faxTransformFaultId) {
		this.faxTransformFaultId = faxTransformFaultId;
	}

	public String getFaxTransformFaultCode() {
		return faxTransformFaultCode;
	}

	public void setFaxTransformFaultCode(String faxTransformFaultCode) {
		this.faxTransformFaultCode = faxTransformFaultCode;
	}

	public String getFaxTransformFaultMessage() {
		return faxTransformFaultMessage;
	}

	public void setFaxTransformFaultMessage(String faxTransformFaultMessage) {
		this.faxTransformFaultMessage = faxTransformFaultMessage;
	}

	public String getEmailSendFaultId() {
		return emailSendFaultId;
	}

	public void setEmailSendFaultId(String emailSendFaultId) {
		this.emailSendFaultId = emailSendFaultId;
	}

	public String getEmailSendFaultCode() {
		return emailSendFaultCode;
	}

	public void setEmailSendFaultCode(String emailSendFaultCode) {
		this.emailSendFaultCode = emailSendFaultCode;
	}

	public String getEmailSendFaultMessage() {
		return emailSendFaultMessage;
	}

	public void setEmailSendFaultMessage(String emailSendFaultMessage) {
		this.emailSendFaultMessage = emailSendFaultMessage;
	}

	public String getEmailListenFaultId() {
		return emailListenFaultId;
	}

	public void setEmailListenFaultId(String emailListenFaultId) {
		this.emailListenFaultId = emailListenFaultId;
	}

	public String getEmailListenFaultCode() {
		return emailListenFaultCode;
	}

	public void setEmailListenFaultCode(String emailListenFaultCode) {
		this.emailListenFaultCode = emailListenFaultCode;
	}

	public String getEmailListenFaultMessage() {
		return emailListenFaultMessage;
	}

	public void setEmailListenFaultMessage(String emailListenFaultMessage) {
		this.emailListenFaultMessage = emailListenFaultMessage;
	}

	public String getEmailTimeoutFaultId() {
		return emailTimeoutFaultId;
	}

	public void setEmailTimeoutFaultId(String emailTimeoutFaultId) {
		this.emailTimeoutFaultId = emailTimeoutFaultId;
	}

	public String getEmailTimeoutFaultCode() {
		return emailTimeoutFaultCode;
	}

	public void setEmailTimeoutFaultCode(String emailTimeoutFaultCode) {
		this.emailTimeoutFaultCode = emailTimeoutFaultCode;
	}

	public String getEmailTimeoutFaultMessage() {
		return emailTimeoutFaultMessage;
	}

	public void setEmailTimeoutFaultMessage(String emailTimeoutFaultMessage) {
		this.emailTimeoutFaultMessage = emailTimeoutFaultMessage;
	}

	public String getEmailUpdateFaultId() {
		return emailUpdateFaultId;
	}

	public void setEmailUpdateFaultId(String emailUpdateFaultId) {
		this.emailUpdateFaultId = emailUpdateFaultId;
	}

	public String getEmailUpdateFaultCode() {
		return emailUpdateFaultCode;
	}

	public void setEmailUpdateFaultCode(String emailUpdateFaultCode) {
		this.emailUpdateFaultCode = emailUpdateFaultCode;
	}

	public String getEmailUpdateFaultMessage() {
		return emailUpdateFaultMessage;
	}

	public void setEmailUpdateFaultMessage(String emailUpdateFaultMessage) {
		this.emailUpdateFaultMessage = emailUpdateFaultMessage;
	}

	public String getOnResultFaultId() {
		return onResultFaultId;
	}

	public void setOnResultFaultId(String onResultFaultId) {
		this.onResultFaultId = onResultFaultId;
	}

	public String getOnResultFaultCode() {
		return onResultFaultCode;
	}

	public void setOnResultFaultCode(String onResultFaultCode) {
		this.onResultFaultCode = onResultFaultCode;
	}

	public String getOnResultFaultMessage() {
		return onResultFaultMessage;
	}

	public void setOnResultFaultMessage(String onResultFaultMessage) {
		this.onResultFaultMessage = onResultFaultMessage;
	}

	public String getUnknownChannelFaultId() {
		return unknownChannelFaultId;
	}

	public void setUnknownChannelFaultId(String unknownChannelFaultId) {
		this.unknownChannelFaultId = unknownChannelFaultId;
	}

	public String getUnknownChannelFaultCode() {
		return unknownChannelFaultCode;
	}

	public void setUnknownChannelFaultCode(String unknownChannelFaultCode) {
		this.unknownChannelFaultCode = unknownChannelFaultCode;
	}

	public String getUnknownChannelFaultMessage() {
		return unknownChannelFaultMessage;
	}

	public void setUnknownChannelFaultMessage(String unknownChannelFaultMessage) {
		this.unknownChannelFaultMessage = unknownChannelFaultMessage;
	}

	public String getRuntimeFaultId() {
		return runtimeFaultId;
	}

	public void setRuntimeFaultId(String runtimeFaultId) {
		this.runtimeFaultId = runtimeFaultId;
	}

	public String getRuntimeFaultCode() {
		return runtimeFaultCode;
	}

	public void setRuntimeFaultCode(String runtimeFaultCode) {
		this.runtimeFaultCode = runtimeFaultCode;
	}

	public String getRuntimeFaultMessage() {
		return runtimeFaultMessage;
	}

	public void setRuntimeFaultMessage(String runtimeFaultMessage) {
		this.runtimeFaultMessage = runtimeFaultMessage;
	}

	public String getCatchAllFaultId() {
		return catchAllFaultId;
	}

	public void setCatchAllFaultId(String catchAllFaultId) {
		this.catchAllFaultId = catchAllFaultId;
	}

	public String getCatchAllFaultCode() {
		return catchAllFaultCode;
	}

	public void setCatchAllFaultCode(String catchAllFaultCode) {
		this.catchAllFaultCode = catchAllFaultCode;
	}

	public String getCatchAllFaultMessage() {
		return catchAllFaultMessage;
	}

	public void setCatchAllFaultMessage(String catchAllFaultMessage) {
		this.catchAllFaultMessage = catchAllFaultMessage;
	}

}
