package ca.bc.gov.ag.efax.mail.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    void testDecodeString() throws Exception {
        assertEquals("", StringUtils.decodeString(null));
        assertEquals("", StringUtils.decodeString(""));
        assertEquals("asdf", StringUtils.decodeString("asdf"));
        assertEquals("line1line2", StringUtils.decodeString("line1\r\nline2"));
        assertEquals("line1\tline2", StringUtils.decodeString("line1\tline2"));
        assertEquals("<jobId></jobId>", StringUtils.decodeString("<jobId></jobId>"));
        assertEquals("<jobId></jobId>", StringUtils.decodeString("&lt;jobId&gt;&lt;/jobId&gt;"));
    }
    
}
