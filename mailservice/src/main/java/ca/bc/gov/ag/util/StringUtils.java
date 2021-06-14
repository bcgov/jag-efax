package ca.bc.gov.ag.util;

public class StringUtils {

	public static String normalizeUUID(String uuid) {
		String newuuid = uuid.replaceAll("\\p{Punct}", "");
		return newuuid;
	}

	// NOT USED
	public static String getMimeType(String format) {
		if (format.equalsIgnoreCase("pdf")) // check the out type
			return "application/pdf";
		else if (format.equalsIgnoreCase("audio_basic"))
			return "audio/basic";
		else if (format.equalsIgnoreCase("audio_wav"))
			return "audio/wav";
		else if (format.equalsIgnoreCase("image_gif"))
			return "image/gif";
		else if (format.equalsIgnoreCase("image_jpeg"))
			return "image/jpeg";
		else if (format.equalsIgnoreCase("image_bmp"))
			return "image/bmp";
		else if (format.equalsIgnoreCase("image_x-png"))
			return "image/x-png";
		else if (format.equalsIgnoreCase("msdownload"))
			return "application/x-msdownload";
		else if (format.equalsIgnoreCase("video_avi"))
			return "video/avi";
		else if (format.equalsIgnoreCase("video_mpeg"))
			return "video/mpeg";
		else if (format.equalsIgnoreCase("html"))
			return "text/html";
		else if (format.equalsIgnoreCase("xml"))
			return "text/xml";
		else
			return "UNDEFINED";
	}

}
