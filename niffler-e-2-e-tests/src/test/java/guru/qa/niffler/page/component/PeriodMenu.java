package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class PeriodMenu extends BaseComponent<PeriodMenu> {

    public PeriodMenu() {
        super($("#menu-period"));
    }

    @Step("Select period")
    public void selectPeriod(DataFilterValues period) {
        self.$(String.format("li[data-value='%s']", period)).click();
    }
}
