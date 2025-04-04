package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage {

    private final SelenideElement imageInput = $("#image__input");
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
}
