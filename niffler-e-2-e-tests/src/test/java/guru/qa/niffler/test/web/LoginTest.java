package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class LoginTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        String username = "duck";
        String password = "12345";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(username, password)
                .checkLoginIsSuccessful();
    }

    @Test
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        String username = "abc";
        String password = "bcd";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(username, password);

        LoginPage loginPage = new LoginPage();
        loginPage.checkWrongUserError();
    }
}
