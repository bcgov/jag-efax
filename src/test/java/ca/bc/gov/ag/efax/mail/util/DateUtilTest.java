package ca.bc.gov.ag.efax.mail.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtilTest {

    @Test
    public void toXMLGregorianCalendarTest() throws DatatypeConfigurationException, ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = format.parse("2021-07-07 11:15:00");

        XMLGregorianCalendar toXmlGregorianCalendar = DateUtil.toXMLGregorianCalendar(date);

        Assertions.assertNotNull(toXmlGregorianCalendar);
        Assertions.assertEquals(toXmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime().compareTo(DatatypeFactory.newInstance().newXMLGregorianCalendar(2021, 7, 7, 11, 15, 0, 0, 0).toGregorianCalendar().toZonedDateTime().toLocalDateTime()), 0);

    }

}
