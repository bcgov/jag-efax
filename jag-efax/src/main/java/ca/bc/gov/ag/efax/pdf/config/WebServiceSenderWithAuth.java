package ca.bc.gov.ag.efax.pdf.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Base64Utils;
import org.springframework.ws.transport.http.HttpUrlConnectionMessageSender;

import java.io.IOException;
import java.net.HttpURLConnection;

@Configuration
public class WebServiceSenderWithAuth extends HttpUrlConnectionMessageSender {

    @Value("${aem.output.username}")
    private String username;

    @Value("${aem.output.password}")
    private String password;

    @Override
    protected void prepareConnection(HttpURLConnection connection) throws IOException {
        String input = username + ":" + password;
        String auth = Base64Utils.encodeToString(input.getBytes());
        connection.setRequestProperty("Authorization", "Basic " + auth);

        super.prepareConnection(connection);
    }
}
