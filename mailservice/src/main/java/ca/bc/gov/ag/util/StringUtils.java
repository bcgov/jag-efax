package ca.bc.gov.ag.util;

public class StringUtils {

	public static String normalizeUUID(String uuid) {
		String newuuid = uuid.replaceAll("\\p{Punct}", "");
		return newuuid;
	}

}
