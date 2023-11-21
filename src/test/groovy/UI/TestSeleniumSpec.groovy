package UI

import org.openqa.selenium.By
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.safari.SafariDriver
import spock.lang.Specification

import java.util.concurrent.TimeUnit

class TestSeleniumSpec extends Specification {
    private WebDriver driver;

    def setup() {
        driver = new SafariDriver()
        driver.manage().window().maximize()
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS)
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS)
    }

    def cleanup() {
        driver.quit()
    }

    def 'should add todo'() {
        given:
            driver.get('https://todomvc.com/examples/angularjs/#/')
            def todoInput = driver.findElement(By.cssSelector("input[class\$='ng-touched']"))
        when:
            todoInput.sendKeys('test')
            todoInput.sendKeys(Keys.ENTER)
            def createdTodo = driver.findElement(By.cssSelector("label[class='ng-binding']"))
        then:
            createdTodo.isDisplayed()
    }
}
