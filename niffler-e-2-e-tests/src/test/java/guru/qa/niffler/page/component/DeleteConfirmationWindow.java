package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class DeleteConfirmationWindow {

    private final SelenideElement self = $("div[role='dialog']");
    private final SelenideElement deleteBtn = self.$(byText("Delete"));
    private final SelenideElement cancelBtn = self.$(byText("Cancel"));

    public void confirmDeletion(){
        deleteBtn.click();
    }
}
