package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MainPage {

    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final ElementsCollection spendingLabels = $("#legend-container").$$("li");
    private final SelenideElement spendingDiagram = $("canvas[role='img']");
    private final SelenideElement deleteButton = $("#delete");
    private final SelenideElement dialogWindow = $("div[role='dialog']");

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public MainPage deleteSpending(String spendingDescription) {
        checkSpending(spendingDescription)
                .clickDeleteButton()
                .confirmDeletion();
        return this;
    }

    public MainPage checkSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .$$("td")
                .get(0)
                .click();
        return this;
    }

    public MainPage clickDeleteButton() {
        deleteButton.click();
        return this;
    }

    public MainPage confirmDeletion() {
        dialogWindow.$(byText("Delete")).click();
        return this;
    }

    public MainPage checkThatTableContains(String spendingDescription) {
        tableRows.find(text(spendingDescription))
                .should(visible);
        return this;
    }

    public MainPage checkLoginIsSuccessful() {
        $(byText("Statistics")).shouldBe(visible);
        $(byText("History of Spendings")).shouldBe(visible);
        return this;
    }

    public MainPage checkSpendingDiagram(BufferedImage expected) throws IOException {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        BufferedImage actual = ImageIO.read(spendingDiagram.screenshot());

        assertFalse(new ScreenDiffResult(
                expected,
                actual
        ));
        return this;
    }

    public MainPage checkSpendingLabelsAreVisible(String...labels) {
        spendingLabels.shouldHave(textsInAnyOrder(labels));
        return this;
    }
}
