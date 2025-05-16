package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProfilePage {

    private final SelenideElement imageInput = $("#image__input");
    private final SelenideElement avatar = $("button[aria-label='Menu']").$("img");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement saveBtn = $("button[type='submit']");
    private final SelenideElement archiveToggle = $("input[type='checkbox']");
    private final SelenideElement categoryInput = $("#category");
    private final ElementsCollection categories = $$(".MuiChip-label");

    public ProfilePage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    public ProfilePage saveChanges() {
        saveBtn.click();
        return this;
    }

    public ProfilePage switchArchiveToggle() {
        archiveToggle.click();
        return this;
    }

    public ProfilePage uploadAvatar(String path) {
        imageInput.uploadFromClasspath(path);
        return this;
    }

    public ProfilePage addNewCategory(String category) {
        categoryInput.setValue(category).pressEnter();
        return this;
    }

    public void checkCategoryIsVisible(String category) {
        categories.findBy(text(category))
                .shouldBe(visible);
    }

    public void checkCategoryIsNotVisible(String category) {
        categories.findBy(text(category))
                .shouldNotBe(visible);
    }

    public void checkProfileAvatar(BufferedImage expected) throws IOException {
        BufferedImage actual = ImageIO.read(avatar.screenshot());
        assertFalse(new ScreenDiffResult(
                expected,
                actual
        ));
    }
}
