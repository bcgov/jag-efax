package ca.bc.gov.ag.efax.mail.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateUtil {

    /**
     * Converts a standard java.util.Date to a java.util.XMLGregorianCalendar.
     * 
     * @param date
     * @return
     * @throws DatatypeConfigurationException if the DatatypeFactory could not be instantiated.
     */
    public static XMLGregorianCalendar toXMLGregorianCalendar(Date date) throws DatatypeConfigurationException {
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
    }

    /** Returns a new date with the specified number of minutes added or subtracted (if negative). */
    public static Date addMinutes(Date date, int amount) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, amount);
        return calendar.getTime();
    }

}
