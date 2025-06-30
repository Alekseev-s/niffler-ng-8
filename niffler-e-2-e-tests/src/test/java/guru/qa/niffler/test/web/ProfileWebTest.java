package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;

@WebTest
public class ProfileWebTest {

    private static final Config CFG = Config.getInstance();

    @User(
            categories = @Category(
                    name = "archived",
                    archived = true
            )
    )
    @ApiLogin
    @Test
    void archivedCategoryShouldPresentInCategoriesList(UserJson user) {
        final CategoryJson category = user.testData().categories().getFirst();

        Selenide.open(CFG.profileUrl(), ProfilePage.class)
                .checkCategoryIsNotVisible(category.name())
                .switchArchiveToggle()
                .checkCategoryIsVisible(category.name());
    }

    @User(
            categories = @Category(
                    name = "active",
                    archived = false
            )
    )
    @ApiLogin
    @Test
    void activeCategoryShouldPresentInCategoriesList(UserJson user) {
        final CategoryJson category = user.testData().categories().getFirst();

        Selenide.open(CFG.profileUrl(), ProfilePage.class)
                .checkCategoryIsVisible(category.name())
                .switchArchiveToggle()
                .checkCategoryIsVisible(category.name());
    }

    @User
    @ApiLogin
    @ScreenShotTest(value = "img/expected-avatar.png",
    rewriteExpected = true)
    void userAvatarShouldBeVisible(BufferedImage expected) throws IOException {
        Selenide.open(CFG.profileUrl(), ProfilePage.class)
                .uploadAvatar("img/avatar.jpg")
                .saveChanges()
                .checkProfileAvatar(expected);
    }

    @User
    @ApiLogin
    @ScreenShotTest(
            value = "img/expected-avatar.png")
    void editProfile(BufferedImage expected) throws IOException {
        final String name = RandomDataUtils.getRandomUsername();

        Selenide.open(CFG.profileUrl(), ProfilePage.class)
                .uploadAvatar("img/avatar.jpg")
                .setName(name)
                .saveChanges()
                .checkAlertMessage("Profile successfully updated")
                .checkProfileAvatar(expected)
                .checkName(name);
    }
}
