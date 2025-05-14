package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;

@ExtendWith(BrowserExtension.class)
public class SpendingWebTest {

  private static final Config CFG = Config.getInstance();

  @User(
          username = "duck",
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Niffler 2.0",
                  amount = 89000.00,
                  currency = CurrencyValues.RUB
          )
  )
  @Test
  void spendingDescriptionShouldBeUpdatedByTableAction(SpendJson spend) {
    final String newDescription = "Обучение Niffler NG";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin("duck", "12345");

    MainPage mainPage = new MainPage();
    mainPage.editSpending(spend.description())
        .editDescription(newDescription);

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
  @ScreenShotTest("img/expected-stat.png")
  void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .checkSpendingDiagram(expected)
            .checkSpendingLabelsAreVisible("Обучение 79990 ₽");
  }

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Niffler 2.0",
                  amount = 79990,
                  currency = CurrencyValues.RUB
          )
  )
  @ScreenShotTest("img/delete-expected-stat.png")
  void checkDeletedStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .deleteSpending(user.testData().spendings().get(0).description())
            .checkSpendingDiagram(expected);
  }

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Niffler 2.0",
                  amount = 79990,
                  currency = CurrencyValues.RUB
          )
  )
  @ScreenShotTest(
          value = "img/edit-expected-stat.png",
          rewriteExpected = true
  )
  void checkEditedStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
    final int newAmount = 50000;

    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .editSpending(user.testData().spendings().get(0).description())
            .editAmount(newAmount);

    Selenide.open(CFG.frontUrl(), MainPage.class)
            .checkSpendingDiagram(expected)
            .checkSpendingLabelsAreVisible("Обучение 50000 ₽");
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
  @ScreenShotTest("img/archived-expected-stat.png")
  void checkArchivedStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .checkSpendingDiagram(expected)
            .checkSpendingLabelsAreVisible("Обучение 79990 ₽", "Archived 21000 ₽");
  }
}
