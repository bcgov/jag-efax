package ca.bc.gov.efax;

import ca.bc.gov.efax.config.BrowserScopePostProcessor;
import ca.bc.gov.efax.page.ExchangeLogInPage;
import ca.bc.gov.efax.service.SendFaxService;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TestConfig {

    @Value("${default.timeout:30}")
    private int timeout;

    @Value("${AUTH_PROVIDER:keycloak}")
    private String provider;

    @Bean
    public SendFaxService sendFaxService() {
        return new SendFaxService();
    }


    @Bean
    public ExchangeLogInPage exchangeLogInPage() {
        return new ExchangeLogInPage();
    }

    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new BrowserScopePostProcessor();
    }

    @Bean
    @Scope("browserscope")
    public WebDriver chromeDriver() {
        WebDriverManager.chromedriver().setup();

//        Map<String, Object> prefs = new HashMap<String, Object>();
//        prefs.put("profile.default_content_settings.popups", 0);
//        prefs.put("download.default_directory", Keys.BASE_PATH + Keys.DOWNLOADED_FILES_PATH);

        ChromeOptions options = new ChromeOptions();
       // options.setExperimentalOption("prefs", prefs);
       // options.setHeadless(true);
        options.addArguments("--window-size=1920,1080");

        return new ChromeDriver(options);
    }

    @Bean
    @Scope("prototype")
    public WebDriverWait webDriverWait(WebDriver driver) {
        return new WebDriverWait(driver, this.timeout);
    }
}
