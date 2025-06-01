package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class ProfilePage {

    public static String url = Config.getInstance().frontUrl() + "profile";

    private final SelenideElement imageInput = $("#image__input");
    private final SelenideElement avatar = $("button[aria-label='Menu']").$("img");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement saveBtn = $("button[type='submit']");
    private final SelenideElement archiveToggle = $("input[type='checkbox']");
    private final SelenideElement categoryInput = $("#category");
    private final ElementsCollection categories = $$(".MuiChip-label");

    @Step("Save changes")
    public ProfilePage saveChanges() {
        saveBtn.click();
        return this;
    }

    @Step("Switch archive toggle")
    public ProfilePage switchArchiveToggle() {
        archiveToggle.click();
        return this;
    }

    @Step("Upload avatar")
    public ProfilePage uploadAvatar(String path) {
        imageInput.uploadFromClasspath(path);
        return this;
    }

    @Step("Add new category '{0}'")
    public ProfilePage addNewCategory(String category) {
        categoryInput.setValue(category).pressEnter();
        return this;
    }

    @Step("Set name '{0}'")
    public ProfilePage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Step("Check category '{0} is visible'")
    public ProfilePage checkCategoryIsVisible(String category) {
        categories.findBy(text(category))
                .shouldBe(visible);
        return this;
    }

    @Step("Check category '{0}' is not visible")
    public ProfilePage checkCategoryIsNotVisible(String category) {
        categories.findBy(text(category))
                .shouldNotBe(visible);
        return this;
    }

    @Step("Check profile avatar")
    public ProfilePage checkProfileAvatar(BufferedImage expected) throws IOException {
        BufferedImage actual = ImageIO.read(avatar.screenshot());
        assertFalse(new ScreenDiffResult(
                expected,
                actual
        ));
        return this;
    }

    @Step("Check name '{0}'")
    public ProfilePage checkName(String name) {
        nameInput.shouldHave(text(name));
        return this;
    }
}
