package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
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

@ParametersAreNonnullByDefault
public class MainPage extends BasePage<MainPage> {

    private final ElementsCollection spendingLabels = $("#legend-container").$$("li");
    private final SelenideElement spendingDiagram = $("canvas[role='img']");
    private final SpendingTable spendingTable = new SpendingTable();

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

    @Step("Check spending labels are visible")
    public MainPage checkSpendingLabelsAreVisible(String...labels) {
        spendingLabels.shouldHave(textsInAnyOrder(labels));
        return this;
    }
}
