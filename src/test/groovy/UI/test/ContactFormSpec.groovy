package UI.test

import UI.utils.UiUtils
import com.github.javafaker.Faker
import io.qameta.allure.*
import org.openqa.selenium.By
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.Select
import org.openqa.selenium.support.ui.WebDriverWait
import spock.lang.Shared

@Epic("UI Tests")
@Story("Test cases for ContactForm")
class ContactFormSpec extends UiUtils {
    public static final String CONTACT_URL = "https://practicesoftwaretesting.com/#/contact"

    @Shared
    public static String firstName

    @Shared
    public static String lastName

    @Shared
    public static String userEmail

    @Shared
    public static Faker faker

    @Shared
    public static String messageText

    def setup() {
        driver.get(CONTACT_URL)
        faker = new Faker();
        firstName = faker.name().firstName();
        lastName = faker.name().lastName();
        userEmail = "${UUID.randomUUID().toString()}@mail.com"
        messageText = faker.lorem().characters(51, 249)
    }

    @Severity(SeverityLevel.NORMAL)
    @Description("Test Description: should fill form and send it")
    def 'C3 - should fill form and send it'() {
        given:
            takeScreenshot()
            def firstNameInput = By.xpath("//*[@id=\"first_name\"]")
            def lastNameInput = By.xpath("//*[@id=\"last_name\"]")
            def emailInput = By.xpath("//*[@id=\"email\"]")
            def subjectSelect = By.xpath("//*[@id=\"subject\"]")
            def message = By.xpath("//*[@id=\"message\"]")
            def sendBtn = By.xpath("/html/body/app-root/div/app-contact/div/div/div/form/div/div[2]/div[4]/input")
        when: "all(expect file input) inputs are filled"
            driver.findElement(firstNameInput).sendKeys(firstName)
            driver.findElement(lastNameInput).sendKeys(lastName)
            driver.findElement(emailInput).sendKeys(userEmail)
            def submitElement = driver.findElement(subjectSelect)
            def selectedSubject = new Select(submitElement)
            selectedSubject.selectByVisibleText("Return")
            driver.findElement(message).sendKeys(messageText)
            takeScreenshot()
        then: "click button to send form"
            driver.findElement(sendBtn).click()
        and: "correct form send message should be visible"
            new WebDriverWait(driver, TIMEOUT).until(ExpectedConditions.visibilityOfElementLocated(By.xpath("/html/body/app-root/div/app-contact/div/div/div/div")))
            takeScreenshot()
    }
}
