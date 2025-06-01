package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

    public Header() {
        super($("#root header"));
    }

    private final SelenideElement menuBtn = self.$("svg[data-testid='PersonIcon']");
    private final SelenideElement addSpendingBtn = self.$("a[href='/spending']");
    private final SelenideElement mainPageBtn = self.$("a[href='/main']");
    private final AccountMenu accountMenu = new AccountMenu();

    public void checkHeaderText() {
        self.$("h1").shouldHave(text("Niffler"));
    }

    @Step("Open Friends page")
    public FriendsPage toFriendsPage() {
        menuBtn.click();
        return accountMenu.toFriendsPage();
    }

    @Step("Open All Peoples page")
    public FriendsPage toAllPeoplesPage() {
        menuBtn.click();
        return accountMenu.toAllPeoplesPage();
    }

    @Step("Open Profile page")
    public ProfilePage toProfilePage() {
        menuBtn.click();
        return accountMenu.toProfilePage();
    }

    @Step("Sign out")
    public LoginPage signOut() {
        menuBtn.click();
        return accountMenu.signOut();
    }

    @Step("Add new spending")
    public EditSpendingPage addSpendingPage() {
        addSpendingBtn.click();
        return new EditSpendingPage();
    }

    @Step("Go to main page")
    public MainPage toMainPage() {
        mainPageBtn.click();
        return new MainPage();
    }
}
