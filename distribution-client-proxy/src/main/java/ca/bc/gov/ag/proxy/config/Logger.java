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

    public void log(final String msg) {
        SimpleDateFormat f = new SimpleDateFormat(datePattern);

        try (FileWriter flog = new FileWriter(getLogFilePath(), true)) {

            flog.write(f.format(new java.util.Date()) + ": " + msg + nlChar);

            System.out.println(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void info(final String msg) {
        log(msg);
    }

    public String getLogFilePath(){
        return userDir + filesepChar + outputFileName;
    }
}
