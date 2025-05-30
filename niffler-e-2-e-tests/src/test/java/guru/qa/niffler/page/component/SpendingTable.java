package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SpendingTable {

    private final SelenideElement self = $("#spendings");
    private final SelenideElement periodBtn = self.$("#period");
    private final SelenideElement deleteBtn = self.$("#delete");
    private final ElementsCollection tableRows = self.$$("tbody tr");
    private final SearchField searchField = new SearchField();
    private final PeriodMenu periodMenu = new PeriodMenu();
    private final DeleteConfirmationWindow deleteConfirmationWindow = new DeleteConfirmationWindow();

    @Step("Select period '{0}'")
    public SpendingTable selectPeriod(DataFilterValues period) {
        periodBtn.click();
        periodMenu.selectPeriod(period);
        return this;
    }

    @Step("Edit spending with description '{0}'")
    public EditSpendingPage editSpending(String description) {
        searchSpendingByDescription(description)
                .tableRows
                .find(text(description))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    @Step("Delete spending with description '{0}'")
    public SpendingTable deleteSpending(String description) {
        searchSpendingByDescription(description)
                .tableRows
                .find(text(description))
                .$$("td")
                .get(0)
                .click();
        deleteBtn.click();
        deleteConfirmationWindow.confirmDeletion();
        return this;
    }

    @Step("Search spending with description '{0}'")
    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        return this;
    }

    @Step("Check that table contains '{0}'")
    public SpendingTable checkTableContains(String expectedSpend) {
        tableRows.find(text(expectedSpend))
                .shouldHave(text(expectedSpend));
        return this;
    }

    @Step("Check that table size is '{0}'")
    public SpendingTable checkTableSize(int expectedSize) {
        tableRows.shouldHave(size(expectedSize));
        return this;
    }
}
