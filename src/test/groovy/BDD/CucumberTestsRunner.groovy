package BDD

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

@CucumberOptions(
        glue = ["BDD.steps"],
        features = ["src/test/resources/BDD/features"],
        plugin = ["pretty", "html:target/results/cucumber-reports/html-report", "json:target/results//cucumber-reports/cucumber.json", "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"],
        monochrome = true
)
@RunWith(Cucumber.class)
class CucumberTestsRunner {

}