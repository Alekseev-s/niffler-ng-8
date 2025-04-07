package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.RegistrationPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class RegistrationWebTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void shouldRegisterNewUser() {
        String username = RandomDataUtils.getRandomUsername();
        String password = RandomDataUtils.getRandomPassword();

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
        String username = RandomDataUtils.getRandomUsername();
        String password = RandomDataUtils.getRandomPassword();
        String confirmPassword = RandomDataUtils.getRandomPassword();

        RegistrationPage registrationPage = Selenide.open(CFG.registrationUrl(), RegistrationPage.class)
                .doRegistration(username, password, confirmPassword);

        registrationPage.checkPasswordNotEqualError();
    }
}
