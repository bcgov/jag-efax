package ca.bc.gov.ag.efax.mail.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FaxUtilsTest {

    @Test
    public void getFaxDestinationTest() {
       String faxDestination =  FaxUtils.getFaxDestination("IMCEAFAX", "testuser", "402505555555@gov.bc.ca");
        Assertions.assertNotNull(faxDestination);
        Assertions.assertEquals("402505555555@gov.bc.ca", faxDestination);
    }
}

