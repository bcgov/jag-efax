package ca.bc.gov.efax.bdd;

import ca.bc.gov.efax.page.BasePage;
import org.junit.After;

public class CucumberHooks extends BasePage {

   // @After("@frontend")
    public void afterScenario() {
        if (this.driver != null)
            this.driver.quit();
    }
}
