package ca.bc.gov.ag.efax.mail.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import ca.bc.gov.ag.efax.mail.config.ExchangeProperties;
import ca.bc.gov.ag.efax.mail.model.MailMessage;
import ca.bc.gov.ag.efax.ws.config.SpringContext;

public class FileUtils {

    public static String getTempFilename(MailMessage mailMessage, int index) {
        return "tmp_attach_" + StringUtils.normalizeUUID(mailMessage.getUuid()) + "_" + index + ".pdf";
    }

    public static File getTempFile(MailMessage mailMessage, int index) {
        ExchangeProperties exchangeProperties = SpringContext.getBean(ExchangeProperties.class);

        String tempFilename = getTempFilename(mailMessage, index);
        File tempFile = new File(exchangeProperties.getTempDirectory() + tempFilename);
        return tempFile;
    }

    public static void deleteTempFile(MailMessage mailMessage, int index) {
        File tempFile = getTempFile(mailMessage, index);
        tempFile.delete();
    }

    public static byte[] fileToByteArray(File attachmentFile) throws Exception {
        ByteArrayOutputStream ous = null;
        InputStream ios = null;

        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(attachmentFile);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
            ous.flush();
        } finally {
            try {
                if (ous != null)
                    ous.close();
            } catch (IOException e) {
            }

            try {
                if (ios != null)
                    ios.close();
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }
}
