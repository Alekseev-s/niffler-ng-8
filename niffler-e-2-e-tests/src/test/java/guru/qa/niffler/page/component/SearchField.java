package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SearchField {

    private final SelenideElement self = $("input[placeholder='Search']").parent().parent();
    private final SelenideElement searchField = self.$("input[placeholder='Search']");
    private final SelenideElement searchButton = self.$("#input-submit");
    private final SelenideElement clearButton = self.$("input-clear");

    @Step("Search by query '{0}'")
    public SearchField search(String query) {
        searchField.setValue(query).pressEnter();
        return this;
    }

    @Step("Clear search input")
    public SearchField clearIfNotEmpty() {
        clearButton.shouldBe(visible).click();
        return this;
    }
}
