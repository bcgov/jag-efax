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

}
