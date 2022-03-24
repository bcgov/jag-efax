package ca.bc.gov.ag.efax.mail.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class StringUtils {

	public static String normalizeUUID(String uuid) {
		String newuuid = uuid.replaceAll("\\p{Punct}", "");
		return newuuid;
	}
	
	/**
	 * Will convert an Object (POJO) to an XML string if possible, otherwise simply calls toString() on the object.
	 */
	public static String toXMLString(Object obj) {
	    try {
            return new XmlMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return obj.toString();
        }
	}
	
	/**
	 * A helper method to remove all carriage returns and replace all escaped < > symbols.
	 * @param str
	 */
	public static String decodeString(String str) {
	    if (str == null) {
	        str = "";
	    }
	    return str
            .replaceAll("\\r", "").replaceAll("\\n", "")
            .replaceAll("&lt;", "<").replaceAll("&gt;", ">");
	}
	
	/**
	 * A helper method to remove all whitespace characters from a given string.
	 */
	public static String removeWhiteSpace(String str) {
        return org.apache.commons.lang3.StringUtils.defaultString(str).replaceAll("\\s", "");     
	}

}
