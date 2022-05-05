package config;

import ca.bc.gov.ag.proxy.config.Logger;
import org.joda.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoggerTest {

    @Test
     void testLoggerInfoGeneratesLogFileAndAddsLine() throws IOException {
        Logger logger = new Logger();
        Path path = Paths.get(logger.getLogFilePath());

        path.toFile().delete();

        String msg = LocalDateTime.now().toString();
        logger.info(msg);

        List<String> lines = Files.readAllLines(path);

        boolean hasMessage = lines.stream().anyMatch(s -> s.contains(msg));

        assertTrue(hasMessage);

    }
    @Test
     void testLoggerAppendsLogs() throws IOException {
        Logger logger = new Logger();
        Path path = Paths.get(logger.getLogFilePath());

        String msg = LocalDateTime.now().toString();

        logger.info("Before message");
        logger.info(msg);
        logger.info("After message");

        List<String> lines = Files.readAllLines(path);

        boolean hasMessage = lines.stream().anyMatch(s -> s.contains(msg));

        assertTrue(hasMessage);

    }
}