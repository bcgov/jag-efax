package ca.bc.gov.ag.efax.graph.comp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.serviceclient.GraphServiceClient;

import ca.bc.gov.ag.efax.graph.config.MSGraphProperties;

/**
 * 
 * Provides a GraphServiceClient (Handles token fetch - Token Refresh, etc). 
 * 
 * Reference: https://learn.microsoft.com/en-us/graph/sdks/choose-authentication-providers?tabs=java#client-credentials-provider 
 * 
 */
@Component
public class GraphServiceClientComp {

	private static final Logger logger = LoggerFactory.getLogger(GraphServiceClientComp.class);

	private String clientId;
	private String tenantId;
	private String clientSecret;

	private GraphServiceClient graphClient = null;

	private MSGraphProperties props;

	public GraphServiceClientComp(MSGraphProperties props) {
		this.props = props;
	}

	@jakarta.annotation.PostConstruct
	private void postConstruct() {

		try {

			this.clientId = props.getClientId();
			this.tenantId = props.getAuthority();
			this.clientSecret = props.getSecretKey();

			// The client credentials flow requires that you request the
			// /.default scope, and pre-configure your permissions on the
			// app registration in Azure. An administrator must grant consent
			// to those permissions beforehand.
			final String[] scopes = new String[] { "https://graph.microsoft.com/.default" };

			final ClientSecretCredential credential = new ClientSecretCredentialBuilder().clientId(clientId)
					.tenantId(tenantId).clientSecret(clientSecret).build();

			if (null == scopes || null == credential) {
				throw new Exception("Unexpected error");
			}

			this.graphClient = new GraphServiceClient(credential, scopes);

			logger.info("MS Graph Service Client created.");

		} catch (Exception ex) {
			
			logger.error("Unable to create MS Graph Service client. Check CLientId, Authority and/or Secret. Err: " + ex.getMessage());
		}

	}

	public GraphServiceClient getGraphClient() {
		return graphClient;
	}

}
