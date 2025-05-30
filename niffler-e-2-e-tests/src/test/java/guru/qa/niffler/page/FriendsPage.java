package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@ParametersAreNonnullByDefault
public class FriendsPage {

    private final SelenideElement friendsTab = $("a[href='/people/friends']");
    private final SelenideElement allPeopleTab = $("a[href='/people/all']");
    private final SelenideElement lonelyNifflerImg = $("img[alt='Lonely niffler']");
    private final SelenideElement noUsersTitle = $(byText("There are no users yet"));
    private final SearchField searchField = new SearchField();
    private final ElementsCollection friendsList = $$("#friends tr");
    private final ElementsCollection allPeopleList = $$("#all tr");
    private final ElementsCollection incomeRequestsList = $$("#requests tr");

    @Step("Open all people tab")
    public FriendsPage goToAllPeopleTab() {
        allPeopleTab.click();
        return this;
    }

    @Step("Check friend '{0}' is visible")
    public FriendsPage checkFriendIsVisible(String friendName) {
        friendsList.find(text(friendName)).shouldBe(visible);
        return this;
    }

    @Step("Check income friendship request")
    public FriendsPage checkIncomeFriendshipRequest(String friendName) {
        incomeRequestsList.find(text(friendName)).shouldBe(visible);
        return this;
    }

    @Step("Check there are no friends")
    public FriendsPage checkThereAreNoFriends() {
        friendsList.shouldHave(size(0));
        noUsersTitle.shouldBe(visible);
        lonelyNifflerImg.shouldBe(visible);
        return this;
    }

    @Step("Check outcome friendship request")
    public FriendsPage checkOutcomeFriendshipRequest(String friendName) {
        allPeopleList.find(text(friendName))
                .shouldBe(visible)
                .$(byText("Waiting..."))
                .shouldBe(visible);
        return this;
    }

    @Step("Check friends page is visible")
    public FriendsPage checkFriendPageIsVisible() {
        friendsTab.shouldBe(visible);
        allPeopleTab.shouldBe(visible);
        return this;
    }

    @Step("Search friend '{0}'")
    public FriendsPage searchFriend(String username) {
        searchField.search(username);
        return this;
    }

    @Step("Accept friendship")
    public FriendsPage acceptFriendship() {
        incomeRequestsList.get(0)
                .$(byText("Accept"))
                .click();
        return this;
    }

    @Step("Decline friendship")
    public FriendsPage declineFriendship() {
        incomeRequestsList.get(0)
                .$(byText("Decline"))
                .click();
        $("div[role='dialog']").$(byText("Decline"))
                .click();
        return this;
    }
}
