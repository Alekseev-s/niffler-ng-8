package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import guru.qa.niffler.page.component.CurrencyMenu;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage {

  private final SelenideElement amountInput = $("#amount");
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement categoryInput = $("#category");
  private final SelenideElement currencyBtn = $("#currency");
  private final SelenideElement cancelBtn = $("#cancel");
  private final SelenideElement saveBtn = $("#save");
  private final CurrencyMenu currencyMenu = new CurrencyMenu();
  private final Calendar calendar = new Calendar();

  @Step("Set new spending description: '{0}'")
  public void editDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    saveBtn.click();
  }

  @Step("Edit spending amount: '{0}'")
  public void editAmount(int amount) {
    amountInput.clear();
    amountInput.setValue(String.valueOf(amount));
    saveBtn.click();
  }

  @Step("Add new spending")
  public MainPage addSpending(String categoryName, String description) {
    amountInput.setValue("1000");
    categoryInput.setValue(categoryName);
    descriptionInput.setValue(description);
    saveBtn.click();
    return new MainPage();
  }
}
