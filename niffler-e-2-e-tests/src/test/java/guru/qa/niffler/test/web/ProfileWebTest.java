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

        ProfilePage profilePage = Selenide.open(CFG.profileUrl(), ProfilePage.class);
        profilePage.checkCategoryIsNotVisible(category.name());
        profilePage.switchArchiveToggle();
        profilePage.checkCategoryIsVisible(category.name());
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

        ProfilePage profilePage = Selenide.open(CFG.profileUrl(), ProfilePage.class);
        profilePage.checkCategoryIsVisible(categories[0].name());
        profilePage.switchArchiveToggle();
        profilePage.checkCategoryIsVisible(categories[0].name());
    }

    @User
    @ScreenShotTest("img/expected-avatar.png")
    void userAvatarShouldBeVisible(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password())
                .checkLoginIsSuccessful();

        ProfilePage profilePage = Selenide.open(CFG.profileUrl(), ProfilePage.class)
                .uploadAvatar("img/avatar.jpg")
                .saveChanges();

        profilePage.checkProfileAvatar(expected);
    }
}
