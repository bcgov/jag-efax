package ca.bc.gov.efax.page;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.beans.factory.annotation.Value;

public class ExchangeLogInPage extends BasePage {

    @Value("${EXCHANGE_URL:}")
    private String exchangeUrl;

    @Value("${EXCHANGE_USERNAME:}")
    private String exchangeUsername;

    @Value("${EXCHANGE_PASSWORD:}")
    private String exchangePassword;


    @FindBy(id = "username")
    private WebElement username;

    @FindBy(id = "password")
    private WebElement password;

    @FindBy(id = "SubmitCreds")
    private WebElement submitBtn;

    public void sigInToOutlook() {

        this.driver.get(exchangeUrl);
        username.sendKeys(exchangeUsername);
        password.sendKeys(exchangePassword);
        submitBtn.click();

        wait.until(ExpectedConditions.titleIs("e1a.gov.bc.ca"));

    }
}
