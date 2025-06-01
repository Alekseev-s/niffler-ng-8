package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class RegistrationPage {

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement submitPasswordInput = $("#passwordSubmit");
    private final SelenideElement signUpBtn = $("button[type='submit']");
    private final SelenideElement signInBtn = $("a.form_sign-in");
    private final SelenideElement successMessage = $("p.form__paragraph_success");

    @Step("Register new user")
    public RegistrationPage doRegistration(String username, String password, String submitPassword) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitPasswordInput.setValue(submitPassword);
        signUpBtn.click();
        return this;
    }

    public LoginPage clickSignInBtn() {
        signInBtn.click();
        return new LoginPage();
    }

    @Step("Check register message")
    public void checkSuccessMessageIsVisible() {
        successMessage.shouldHave(text("Congratulations! You've registered!"));
    }

    @Step("Check username")
    public void checkUsernameExistingError(String username) {
        usernameInput.sibling(0)
                .shouldBe(visible)
                .shouldHave(text(String.format("Username `%s` already exists", username)));
    }

    @Step("Check passwords")
    public void checkPasswordNotEqualError() {
        passwordInput.sibling(1)
                .shouldBe(visible)
                .shouldHave(text("Passwords should be equal"));
    }
}
