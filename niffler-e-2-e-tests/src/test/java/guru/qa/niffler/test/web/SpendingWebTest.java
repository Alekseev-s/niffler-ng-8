package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.spend.Bubble;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

@ExtendWith(BrowserExtension.class)
public class SpendingWebTest {

    private static final Config CFG = Config.getInstance();

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 89000.00,
                    currency = CurrencyValues.RUB
            )
    )
    @ApiLogin
    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
        final SpendJson spending = user.testData().spendings().get(0);
        final String newDescription = "Обучение Niffler NG";

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .editSpending(spending.description())
                .editDescription(newDescription);

        MainPage mainPage = new MainPage();
        mainPage.checkThatTableContains(newDescription);
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 79990,
                    currency = CurrencyValues.RUB
            )
    )
    @ApiLogin
    @ScreenShotTest("img/expected-stat.png")
    void checkStatComponentTest(BufferedImage expected) throws IOException {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .getStatComponent()
                .checkStatisticImage(expected)
                .checkStatBubbles(new Bubble(Color.green, "Обучение 79990 ₽"));
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 79990,
                    currency = CurrencyValues.RUB
            )
    )
    @ApiLogin
    @ScreenShotTest(
            value = "img/delete-expected-stat.png",
            rewriteExpected = true)
    void checkDeletedStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .deleteSpending(user.testData().spendings().get(0).description())
                .checkAlertMessage("Spendings succesfully deleted")
                .getStatComponent()
                .checkStatisticImage(expected);
    }

    @User(
            spendings = @Spending(
                    category = "Обучение",
                    description = "Обучение Niffler 2.0",
                    amount = 79990,
                    currency = CurrencyValues.RUB
            )
    )
    @ApiLogin
    @ScreenShotTest(
            value = "img/edit-expected-stat.png",
            rewriteExpected = true
    )
    void checkEditedStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        final int newAmount = 50000;

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .editSpending(user.testData().spendings().get(0).description())
                .editAmount(newAmount);

        Selenide.open(CFG.frontUrl(), MainPage.class)
                .getStatComponent()
                .checkStatisticImage(expected)
                .checkStatBubbles(new Bubble(Color.green, "Обучение 79990 ₽"));
    }

    @User(
            categories = {
                    @Category(
                            name = "Транспорт",
                            archived = true
                    ),
                    @Category(
                            name = "Продукты",
                            archived = true
                    )
            },
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Niffler 2.0",
                            amount = 79990,
                            currency = CurrencyValues.RUB
                    ),
                    @Spending(
                            category = "Транспорт",
                            description = "Такси",
                            amount = 20000,
                            currency = CurrencyValues.RUB
                    ),
                    @Spending(
                            category = "Продукты",
                            description = "Ужин",
                            amount = 1000,
                            currency = CurrencyValues.RUB
                    )
            }
    )
    @ApiLogin
    @ScreenShotTest("img/archived-expected-stat.png")
    void checkArchivedStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(CFG.frontUrl(), MainPage.class)
                .getStatComponent()
                .checkStatisticImage(expected)
                .checkStatBubbles(new Bubble(Color.green, "Обучение 79990 ₽"), new Bubble(Color.yellow, "Archived 21000 ₽"));
    }
}
