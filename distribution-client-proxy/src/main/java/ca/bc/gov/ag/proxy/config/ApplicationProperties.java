package ca.bc.gov.ag.proxy.config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

public class ApplicationProperties {

    private static final String faxCoverSheetHtmlFilepath;
    private static final String documentStatusHtmlFragmentFilepath;

    static {
        Yaml yaml = new Yaml();
        InputStream inputStream = ApplicationProperties.class.getClassLoader().getResourceAsStream("properties.yml");
        Map<String, Object> properties = yaml.load(inputStream);

        faxCoverSheetHtmlFilepath = (String) properties.get("faxCoverSheetHtmlFilepath");
        documentStatusHtmlFragmentFilepath = (String) properties.get("documentStatusHtmlFragmentFilepath");

    }


    public static String getFaxCoverSheetHtmlFilepath() {
        return faxCoverSheetHtmlFilepath;
    }

    public static String getDocumentStatusHtmlFragmentFilepath() {
        return documentStatusHtmlFragmentFilepath;
    }
}
