package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class AccountMenu {
    private final SelenideElement self = $("#account-menu");
    private final SelenideElement toFriendsBtn = self.$("a[href='/people/friends']");
    private final SelenideElement toAllPeopleBtn = self.$("a[href='/people/all']");
    private final SelenideElement toProfileBtn = self.$("a[href='/profile']");
    private final SelenideElement signOutBtn = self.$(byText("Sign out"));

    public FriendsPage toFriendsPage() {
        toFriendsBtn.click();
        return new FriendsPage();
    }

    public FriendsPage toAllPeoplesPage() {
        toAllPeopleBtn.click();
        return new FriendsPage();
    }

    public ProfilePage toProfilePage() {
        toProfileBtn.click();
        return new ProfilePage();
    }

    public LoginPage signOut() {
        signOutBtn.click();
        return new LoginPage();
    }
}
