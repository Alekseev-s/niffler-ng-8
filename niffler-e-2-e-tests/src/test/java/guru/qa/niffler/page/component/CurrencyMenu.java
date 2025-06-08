package guru.qa.niffler.page.component;

import guru.qa.niffler.model.spend.CurrencyValues;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class CurrencyMenu extends BaseComponent<CurrencyMenu> {

    public CurrencyMenu() {
        super($("#menu-currency"));
    }

    @Step("Select currency")
    public void selectCurrency(CurrencyValues currency) {
        self.$(String.format("li[data-value='%s']", currency)).click();
    }
}
