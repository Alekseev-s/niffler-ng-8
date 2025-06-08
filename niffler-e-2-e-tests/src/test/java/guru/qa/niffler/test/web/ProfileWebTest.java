package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;

@ExtendWith(BrowserExtension.class)
public class ProfileWebTest {

    private static final Config CFG = Config.getInstance();

    @User(
            categories = @Category(
                    name = "archived",
                    archived = true
            )
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        final CategoryJson category = user.testData().categories().getFirst();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .checkLoginIsSuccessful();

        Selenide.open(CFG.profileUrl(), ProfilePage.class)
                .checkCategoryIsNotVisible(category.name())
                .switchArchiveToggle()
                .checkCategoryIsVisible(category.name());
    }

    @User(
            username = "duck",
            categories = @Category(
                    archived = false
            )
    )
    @Test
    void activeCategoryShouldPresentInCategoriesList(CategoryJson[] categories) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin("duck", "12345")
                .checkLoginIsSuccessful();

        Selenide.open(CFG.profileUrl(), ProfilePage.class)
                .checkCategoryIsVisible(categories[0].name())
                .switchArchiveToggle()
                .checkCategoryIsVisible(categories[0].name());
    }

    @User
    @ScreenShotTest(value = "img/expected-avatar.png",
    rewriteExpected = true)
    void userAvatarShouldBeVisible(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .checkLoginIsSuccessful();

        Selenide.open(CFG.profileUrl(), ProfilePage.class)
                .uploadAvatar("img/avatar.jpg")
                .saveChanges()
                .checkProfileAvatar(expected);
    }

    @User
    @ScreenShotTest(
            value = "img/expected-avatar.png")
    void editProfile(UserJson user, BufferedImage expected) throws IOException {
        final String name = RandomDataUtils.getRandomUsername();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .checkLoginIsSuccessful();

        Selenide.open(CFG.profileUrl(), ProfilePage.class)
                .uploadAvatar("img/avatar.jpg")
                .setName(name)
                .saveChanges()
                .checkAlertMessage("Profile successfully updated")
                .checkProfileAvatar(expected)
                .checkName(name);
    }
}
