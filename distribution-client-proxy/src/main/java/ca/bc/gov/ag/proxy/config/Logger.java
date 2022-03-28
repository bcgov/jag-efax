package ca.bc.gov.ag.proxy.config;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class Logger {
    private static final String filesepChar = System.getProperty("file.separator");
    private static final String nlChar = System.getProperty("line.separator");
    private static final String datePattern = "MM/dd/yyyy hh:mm:ss a";
    private static final String outputFileName = "DistributionClient.log";
    private static final String userDir = ".";

    private static final String fileName = userDir + filesepChar + outputFileName;

    public void log(final String msg) {
        SimpleDateFormat f = new SimpleDateFormat(datePattern);

        try (FileWriter flog = new FileWriter(fileName, true)) {

            flog.write(f.format(new java.util.Date()) + ": " + msg + nlChar);

            System.out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void info(final String msg) {
        log(msg);
    }
}
