package ca.bc.gov.ag.dist.ws.config;

import java.util.List;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
@EnableConfigurationProperties(DocumentDistributionProperties.class)
public class WebServiceConfig extends WsConfigurerAdapter {

	public static final String NAMESPACE_URI = "http://ag.gov.bc.ca/DocumentDistributionMainProcess";

	@Bean
	public DocumentDistributionProperties documentDistributionProperties(DocumentDistributionProperties documentDistributionProperties) {
		return documentDistributionProperties;
	}
	
	@Bean
	public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		return new ServletRegistrationBean<>(servlet, "/ws/*");
	}

	@Bean(name = "DocumentDistributionMainProcess")
	public Wsdl11Definition defaultWsdl11Definition() {
		SimpleWsdl11Definition wsdl11Definition = new SimpleWsdl11Definition();
		wsdl11Definition.setWsdl(new ClassPathResource("/DocumentDistributionMainProcess.wsdl"));
		return wsdl11Definition;
	}
	
	@Override
	public void addInterceptors(List<EndpointInterceptor> interceptors) {
        PayloadValidatingInterceptor validatingInterceptor = new PayloadValidatingInterceptor();
        validatingInterceptor.setValidateRequest(true);
        validatingInterceptor.setValidateResponse(true);
        validatingInterceptor.setXsdSchema(xsdSchema());
        interceptors.add(validatingInterceptor);
	}
	
    @Bean
    public XsdSchema xsdSchema(){
        return new SimpleXsdSchema(new ClassPathResource("DocumentDistributionMainProcess.xsd"));
    }
	
}
