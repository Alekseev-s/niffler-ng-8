package guru.qa.niffler.page;

import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    public static final String URL = CFG.frontUrl() + "main";

    private final Header header = new Header();
    private final SpendingTable spendingTable = new SpendingTable();
    private final StatComponent statComponent = new StatComponent();

    public StatComponent getStatComponent() {
        return statComponent;
    }

    public SpendingTable getSpendingTable() {
        return spendingTable;
    }

    @Step("Edit spending '{0}'")
    public EditSpendingPage editSpending(String spendingDescription) {
        return spendingTable.editSpending(spendingDescription);
    }

    @Step("Delete spending '{0}'")
    public MainPage deleteSpending(String spendingDescription) {
        spendingTable.deleteSpending(spendingDescription);
        return this;
    }

    @Step("Check that table contains spending '{0}'")
    public MainPage checkThatTableContains(String spendingDescription) {
        spendingTable.checkTableContains(spendingDescription);
        return this;
    }

    public MainPage checkLoginIsSuccessful() {
        $(byText("Statistics")).shouldBe(visible);
        $(byText("History of Spendings")).shouldBe(visible);
        return this;
    }

    @Step("Check spending diagram")
    public MainPage checkSpendingDiagram(BufferedImage expected) throws IOException {
        statComponent.checkStatisticImage(expected);
        return this;
    }

    @Step("Check spending labels are visible")
    public MainPage checkSpendingLabelsAreVisible(String...labels) {
        statComponent.checkStatisticBubblesContains(labels);
        return this;
    }

    @Step("Check that page is loaded")
    @Nonnull
    public MainPage checkThatPageLoaded() {
        header.getSelf().should(visible).shouldHave(text("Niffler"));
        statComponent.getSelf().should(visible).shouldHave(text("Statistics"));
        spendingTable.getSelf().should(visible).shouldHave(text("History of Spendings"));
        return this;
    }
}
