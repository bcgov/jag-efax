package ca.bc.gov.ag.efax.ws;

import ca.bc.gov.ag.efax.mail.util.DateUtil;
import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.ItemBody;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.models.Recipient;

public class TestUtils {

    public static final String JOB_ID = "9827984792943";
    public static final String SOMEONE = "Someone";
    public static final String SOMEONE_ELSE = "Someone Else";
    public static final String FAX = "fax";
    public static final String TRANSPORT = "5551234567";
    public static final String TEST_SUBJECT = "Test subject";
    public static final String TEST_BODY = "Test body";
    public static final int NUM_PAGES = 1;
    public static final String SAMPLE_V_1_6_PDF = "sample_v1.6.pdf";
    public static final String EXTENSION_1 = "a";
    public static final String EXTENSION_2 = "b";
    public static final int DURATION_IN_MILLI_SECONDS = 1500000;

    public static DocumentDistributionRequest getDocumentDistributionRequest() throws DatatypeConfigurationException {
        DocumentDistributionRequest request = new DocumentDistributionRequest();
        request.setFrom(SOMEONE);
        request.setTo(SOMEONE_ELSE);
        request.setJobId(JOB_ID);
        request.setDateTime(DateUtil.toXMLGregorianCalendar(new Date()));
        request.setTimeout(DatatypeFactory.newInstance().newDuration(DURATION_IN_MILLI_SECONDS));
        request.setChannel(FAX);
        request.setTransport(TRANSPORT);
        request.setSubject(TEST_SUBJECT);
        request.setBody(TEST_BODY);
        request.setNumPages(NUM_PAGES);
        request.setAttachments(new DocumentDistributionRequest.Attachments());
        request.getAttachments().getUri().add(TestUtils.class.getClassLoader().getResource(SAMPLE_V_1_6_PDF).toString());
        request.setExtension1(EXTENSION_1);
        request.setExtension2(EXTENSION_2);
        return request;
    }

    public static Message getMessage()  {
        Message message = new Message();
        message.setFrom(new Recipient());
        message.setBody(new ItemBody());
        message.setSubject(JOB_ID);
        message.setBccRecipients(new ArrayList<Recipient>());
        message.setBodyPreview("BodyPreview");
        message.setCcRecipients(new ArrayList<Recipient>());
        message.setSubject(TEST_SUBJECT);
        message.setAttachments(Arrays.asList(new Attachment()));

        return message;
    }
}
