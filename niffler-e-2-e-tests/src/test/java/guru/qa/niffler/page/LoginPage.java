package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Condition.visible;

@ParametersAreNonnullByDefault
public class LoginPage extends BasePage<LoginPage> {

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitBtn = $("button[type='submit']");

  @Step("Login")
  public MainPage doLogin(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitBtn.click();
    return new MainPage();
  }

  @Step("Check user is not correct")
  public void checkWrongUserError() {
    $("p.form__error")
            .shouldBe(visible)
            .shouldHave(text("Неверные учетные данные пользователя"));
  }
}
