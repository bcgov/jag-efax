package ca.bc.gov.ag.efax;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import ca.bc.gov.ag.efax.graph.config.MSGraphProperties;
import ca.bc.gov.ag.efax.graph.service.MSGraphService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import ca.bc.gov.ag.efax.redis.TestRedisConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestRedisConfiguration.class)
@ActiveProfiles("test")
public class ApplicationNonMockTest {

    @Autowired
    protected MSGraphService msGraphService;

    @Autowired
    MSGraphProperties msGraphProperties;

	@Test
	public void contextLoads() throws Exception {

        assertNotNull(msGraphProperties);
        assertNotNull(msGraphService);
	}
	
}
