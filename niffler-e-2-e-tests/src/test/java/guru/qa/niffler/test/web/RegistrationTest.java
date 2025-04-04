package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.RegistrationPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegistrationTest {

    private static final Config CFG = Config.getInstance();
    private final Faker faker = new Faker();

    @Test
    void shouldRegisterNewUser() {
        String username = faker.name().firstName();
        String password = faker.internet().password(3, 10);

        RegistrationPage registrationPage = Selenide.open(CFG.registrationUrl(), RegistrationPage.class)
                .doRegistration(username, password, password);

        registrationPage.checkSuccessMessageIsVisible();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        String existingUsername = "duck";
        String password = "12345";

        RegistrationPage registrationPage = Selenide.open(CFG.registrationUrl(), RegistrationPage.class)
                .doRegistration(existingUsername, password, password);

        registrationPage.checkUsernameExistingError(existingUsername);
    }

    @Test
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        String username = faker.name().firstName();
        String password = faker.internet().password(3, 10);
        String confirmPassword = faker.internet().password(3, 10);

        RegistrationPage registrationPage = Selenide.open(CFG.registrationUrl(), RegistrationPage.class)
                .doRegistration(username, password, confirmPassword);

        registrationPage.checkPasswordNotEqualError();
    }
}
