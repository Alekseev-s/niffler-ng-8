package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class ProfileWebTest {

    private static final Config CFG = Config.getInstance();
    private final String username = "duck";
    private final String password = "12345";

    @User(
            username = "duck",
            categories = @Category(
                    archived = true
            )
    )
    @Test
    void archivedCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(username, password)
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
    void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(username, password)
                .checkLoginIsSuccessful();

        ProfilePage profilePage = Selenide.open(CFG.profileUrl(), ProfilePage.class);
        profilePage.checkCategoryIsVisible(category.name());
        profilePage.switchArchiveToggle();
        profilePage.checkCategoryIsVisible(category.name());
    }
}
