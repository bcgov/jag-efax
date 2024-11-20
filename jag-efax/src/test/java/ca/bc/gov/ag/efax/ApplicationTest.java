package ca.bc.gov.ag.efax;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;
import org.springframework.core.io.ClassPathResource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class ApplicationTest extends BaseTestSuite {
	@Test
	public void contextLoads() {
		assertNotNull(sentMessageRepository);
	}

	@Test
	public void healthCheck() {
		// assert the health check returns { "status" : "UP" } - this confirms the ws is up.

		JsonNode node = restTemplate.getForObject("/actuator/health", JsonNode.class);
		assertTrue(Status.UP.getCode().equals(node.get("status").asText()));
	}

    @Test
    public void wsdlAvailable() throws IOException {
        // assert the wsdl files available on the classpath is the same as that provided via the webservice - this confirms the ws is working.

        XmlMapper xmlMapper = new XmlMapper();

        // load wsdl file from classpath
        ClassPathResource resource = new ClassPathResource("DocumentDistributionMainProcess.wsdl");
        String wsdlFile = FileUtils.readFileToString(resource.getFile(), StandardCharsets.UTF_8);
        Map<?,?> mappedSrc = xmlMapper.readValue(wsdlFile, Map.class);
        assertFalse(mappedSrc.isEmpty());

        // load wsdl file provided via the web service
        String wsdlWeb = restTemplate.getForObject("/ws/DocumentDistributionMainProcess.wsdl", String.class);
        Map<?,?> mappedTrgt = xmlMapper.readValue(wsdlWeb, Map.class);
        assertFalse(mappedTrgt.isEmpty());

        // both files should be identical (compare files as Maps since whitespace, order of xml structure may be different)
        assertTrue(mappedSrc.equals(mappedTrgt));
    }
	
}
