package ca.bc.gov.ag.proxy;

import ca.bc.gov.ag.efax.ws.model.DocumentDistributionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocumentDistributionRequestBuilderTest {

    private static final String from = "Gabriel Testing from Local";
    private static final String to = "Me, myself, and I";
    private static final String jobId = "9999";
    private static final String sdateTime = "24-MAR-2022 02:16 pm";
    private static final String timeout = "PT25M";
    private static final String schannel = "fax";
    private static final String transport = "7785720693";
    private static final String subject = "Probation Order (OA)(Prison)<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CUNNINGHAM, Richie";
    private static final String fileNumber = "12345";
    private static final String snumPages = "1";
    private static final String attachment = "https://github.com/bcgov/jag-efax/raw/main/jag-efax/src/test/resources/sample_v1.4.pdf";
    private static final String extension1 = "a";
    private static final String extension2 = "b";
    private static final String fromFaxNumber = "(250)940-5373";
    private static final String fromPhoneNumber = "(250)470-6900";
    private static final String documentStatus = null;
    private static final String documentStatusDate = null;
    private static final String receiveFaxCoverPageYn = "Y";
    private DocumentDistributionRequestBuilder populatedBuilder;
    private DocumentDistributionRequestBuilder emptyBuilder;


    @BeforeEach
    void instantiateBuilder() {
        populatedBuilder = new DocumentDistributionRequestBuilder()
                .setFrom(from)
                .setTo(to)
                .setJobId(jobId)
                .setStringDateTime(sdateTime)
                .setTimeout(timeout)
                .setChannel(schannel)
                .setTransport(transport)
                .setSubject(subject)
                .setFaxCoverPage(receiveFaxCoverPageYn)
                .setNumPages(snumPages)
                .setAttachments(attachment)
                .setExtension1(extension1)
                .setExtension2(extension2)
                .setFromFaxNumber(fromFaxNumber)
                .setFromPhoneNumber(fromPhoneNumber)
                .setFileNumber(fileNumber)
                .setDocumentStatus(documentStatus)
                .setDocumentStatusDate(documentStatusDate);
        emptyBuilder = new DocumentDistributionRequestBuilder();
    }

    @Test
    void setNumPagesWhenNocoverPage() {
        int expected = 9;
        emptyBuilder.setNumPages(String.valueOf(expected));
        int actual = emptyBuilder.getNumPages();

        assertEquals(expected, actual);
    }

    @Test
    void setNumPagesWhenHasCoverPage() {
        int actual = populatedBuilder.getNumPages();
        assertEquals(Integer.parseInt(snumPages) + 1, actual);
    }

    @Test
    void setNumPagesWhenSetCoverPageAfterNumberofPages() {
        int expected = 2;
        emptyBuilder.setNumPages(snumPages);
        emptyBuilder.setFaxCoverPage("Y");
        int actual = populatedBuilder.getNumPages();
        assertEquals(expected, actual);
    }

    @Test
    void setFaxCoverPageWithNoDocument() {
        int expected = 1;
        emptyBuilder.setFaxCoverPage("Y");
        int actual = emptyBuilder.getNumPages();
        assertEquals(expected, actual);
    }

    @Test
    void setNumPagesTo0AfterSettingTheCoverPage() {
        int expected = 1;
        emptyBuilder.setFaxCoverPage("Y");
        emptyBuilder.setNumPages("0");
        int actual = emptyBuilder.getNumPages();
        assertEquals(expected, actual);
    }

    @Test
    void setNumPagesTo1WithoutACoverPage() {
        int expected = 1;
        emptyBuilder.setFaxCoverPage("N");
        emptyBuilder.setNumPages("1");
        int actual = emptyBuilder.getNumPages();
        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "values: {argumentsWithNames}")
    @CsvSource({
            "This in an update message,2022-05-12",
            "This in an update message,''",
            "This in an update message,' '",
            "This in an update message,'\t'",
            "This in an update message,'\n'",
    })
    void checkTheDocumentBodyWithTheStatusUpdate(String updateMessage, String updateDate) throws IOException, URISyntaxException {
        String expectedBody = readBodyFile("bodyWithStatus.html");
        expectedBody = expectedBody.replaceAll("%%UPDATE_MESSAGE%%", updateMessage != null ? updateMessage.trim() : "");
        expectedBody = expectedBody.replaceAll("%%UPDATE_DATE%%", updateMessage != null ? updateDate.trim() : "");

        populatedBuilder.setDocumentStatus(updateMessage);
        populatedBuilder.setDocumentStatusDate(updateDate);
        DocumentDistributionRequest build = populatedBuilder.build();

        assertEquals(expectedBody, build.getBody());
    }


    @ParameterizedTest(name = "value: {argumentsWithNames}")
    @ValueSource(strings = {" ", "\t", "\n"})
    @NullAndEmptySource
    void checkTheDocumentBodyWithoutTheStatusUpdate(String updateMessage) throws IOException, URISyntaxException {
        String expectedBody = readBodyFile("bodyWithoutStatus.html");

        expectedBody = expectedBody.replaceAll("%%UPDATE_MESSAGE%%", updateMessage != null ? updateMessage.trim() : "");

        DocumentDistributionRequest build = populatedBuilder.build();
        assertEquals(expectedBody, build.getBody());

    }

    private String readBodyFile(String name) throws IOException {
        String filePath = getClass().getClassLoader().getResource(name).getPath();
        String data = new String(Files.readAllBytes(Paths.get(filePath)));

        return "<![CDATA[" + data + "]]>";
    }
}