package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.spend.CurrencyValues;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class CurrencyMenu {

    private final SelenideElement self = $("#menu-currency");

    @Step("Select currency")
    public void selectCurrency(CurrencyValues currency) {
        self.$(String.format("li[data-value='%s']", currency)).click();
    }
}
