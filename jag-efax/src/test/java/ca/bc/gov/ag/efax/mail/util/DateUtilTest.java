package ca.bc.gov.ag.efax.mail.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.jupiter.api.Test;

public class DateUtilTest {

    @Test
    public void toXMLGregorianCalendarTest() throws DatatypeConfigurationException, ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date = format.parse("2021-07-07 11:15:00");

        XMLGregorianCalendar toXmlGregorianCalendar = DateUtil.toXMLGregorianCalendar(date);

        assertNotNull(toXmlGregorianCalendar);
        assertEquals(toXmlGregorianCalendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime().compareTo(DatatypeFactory.newInstance().newXMLGregorianCalendar(2021, 7, 7, 11, 15, 0, 0, 0).toGregorianCalendar().toZonedDateTime().toLocalDateTime()), 0);
    }

    @Test
    public void testAddDay() throws Exception {
        Date now = Calendar.getInstance().getTime();

        Calendar in1Minute = Calendar.getInstance();
        in1Minute.setTime(now);
        in1Minute.add(Calendar.MINUTE, 1);
        assertEquals(in1Minute.getTime(), DateUtil.addMinutes(now, 1));

        Calendar yesterday = Calendar.getInstance();
        yesterday.setTime(now);
        yesterday.add(Calendar.MINUTE, -1);
        assertEquals(yesterday.getTime(), DateUtil.addMinutes(now, -1));
        
        assertNull(DateUtil.addMinutes(null, 1));
    }
}
