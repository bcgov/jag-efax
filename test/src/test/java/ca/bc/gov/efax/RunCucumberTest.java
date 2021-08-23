package ca.bc.gov.efax;


import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"src/test/resources"},
        glue ={"ca.bc.gov.efax"},
        monochrome = true,
        plugin = {
                "pretty",
                "html:target/cucumber-reports/cucumber-pretty",
                "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
        }
)
public class RunCucumberTest {

}
