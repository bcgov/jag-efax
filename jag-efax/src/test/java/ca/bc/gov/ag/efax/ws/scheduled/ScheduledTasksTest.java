package ca.bc.gov.ag.efax.ws.scheduled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;
import java.util.UUID;

import ca.bc.gov.ag.efax.BaseTestSuite;
import ca.bc.gov.ag.efax.mail.repository.SentMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import ca.bc.gov.ag.efax.mail.model.SentMessage;
import ca.bc.gov.ag.efax.mail.util.DateUtil;
import ca.bc.gov.ag.efax.ws.service.DocumentDistributionService;

public class ScheduledTasksTest extends BaseTestSuite  {

    @Mock
    private DocumentDistributionService documentDistributionService;

    @Autowired
    protected SentMessageRepository sentMessageRepository;
    
    @InjectMocks
    private ScheduledTasks scheduledTasks;
    
    @BeforeEach
    protected void beforeEach() throws Exception {

        ReflectionTestUtils.setField(scheduledTasks, "sentMessageRepository", sentMessageRepository);
        ReflectionTestUtils.setField(scheduledTasks, "faxTimeout", 1500000L); // 25 minutes
        clearInvocations(documentDistributionService);
        sentMessageRepository.deleteAll();
    }

    @Test
    void testSentFaxTimeout_timeout() throws Exception {
        // data setup
        // store a record in redis created 30 minutes ago, timeout is 25 minutes - this should trigger a timeout
        SentMessage sentMessage1 = new SentMessage();
        sentMessage1.setJobId("12345");
        sentMessage1.setUuid(UUID.randomUUID().toString());
        sentMessage1.setCreatedTs(DateUtil.addMinutes(new Date(), -30));
        sentMessageRepository.save(sentMessage1);
        assertEquals(1, sentMessageRepository.count());

        scheduledTasks.sentFaxTimeout();

        // assert callback was called
//        verify(documentDistributionService, times(1)).sendResponseToCallback(any());

        // assert record has been removed
        assertEquals(0, sentMessageRepository.count());
    }

    @Test
    void testSentFaxTimeout_notimeout() throws Exception {
        // data setup
        // store a record in redis created 10 minutes ago, timeout is 25 minutes - this should not trigger a timeout
        SentMessage sentMessage1 = new SentMessage();
        sentMessage1.setJobId("12345");
        sentMessage1.setUuid(UUID.randomUUID().toString());
        sentMessage1.setCreatedTs(DateUtil.addMinutes(new Date(), -10));
        sentMessageRepository.save(sentMessage1);
        assertEquals(1, sentMessageRepository.count());

        scheduledTasks.sentFaxTimeout();

        // assert callback was not called
        verify(documentDistributionService, times(0)).sendResponseToCallback(any());

        // assert record has not been removed
        assertEquals(1, sentMessageRepository.count());
    }
}
