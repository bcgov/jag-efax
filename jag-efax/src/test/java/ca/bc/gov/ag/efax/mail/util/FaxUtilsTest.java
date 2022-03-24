package ca.bc.gov.ag.efax.mail.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FaxUtilsTest {

    @Test
    public void getFaxDestinationTest() {
        Assertions.assertEquals("2505551234@gov.bc.ca", FaxUtils.getFaxDestination("%FAXNUMBER%@gov.bc.ca", "testuser", "2505551234"));
        Assertions.assertEquals("2505551234@gov.bc.ca", FaxUtils.getFaxDestination("%FAXNUMBER%@gov.bc.ca", "testuser", "250 555 1234"));
        Assertions.assertEquals("2505551234@gov.bc.ca", FaxUtils.getFaxDestination("%FAXNUMBER%@gov.bc.ca", "testuser", "(250) 555-1234"));
        Assertions.assertEquals("2505551234@gov.bc.ca", FaxUtils.getFaxDestination("%FAXNUMBER%@gov.bc.ca", "testuser", "250.555.1234"));

        Assertions.assertEquals("IMCEAFAX-Testuser-2505551234@gov.bc.ca",
                FaxUtils.getFaxDestination("IMCEAFAX-%RECIPIENT%-%FAXNUMBER%@gov.bc.ca", "Testuser", "2505551234"));
        Assertions.assertEquals("<Testuser>2505551234@gov.bc.ca",
                FaxUtils.getFaxDestination("<%RECIPIENT%>%FAXNUMBER%@gov.bc.ca", "Testuser", "2505551234"));
        Assertions.assertEquals("<A+20Place+20with+20Spaces>2505551234@gov.bc.ca",
                FaxUtils.getFaxDestination("<%RECIPIENT%>%FAXNUMBER%@gov.bc.ca", "A Place with Spaces", "2505551234"));
        
        // ()"<>:;%,+@ should be replaced with _
        Assertions.assertEquals("<A+20Pl_ce+20_with_+20Sp&cia!+20__Charact&r$>2505551234@gov.bc.ca",
                FaxUtils.getFaxDestination("<%RECIPIENT%>%FAXNUMBER%@gov.bc.ca", "A Pl@ce +with\" Sp&cia! <>Charact&r$", "2505551234"));
    }
}
