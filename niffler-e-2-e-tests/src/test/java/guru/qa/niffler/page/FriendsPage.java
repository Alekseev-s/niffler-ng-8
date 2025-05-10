package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {

    private final SelenideElement friendsTab = $("a[href='/people/friends']");
    private final SelenideElement allPeopleTab = $("a[href='/people/all']");
    private final SelenideElement searchField = $("input[placeholder='Search']");
    private final ElementsCollection friendsList = $$("#friends tr");
    private final ElementsCollection allPeopleList = $$("#all tr");
    private final ElementsCollection incomeRequestsList = $$("#requests tr");
    private final SelenideElement lonelyNifflerImg = $("img[alt='Lonely niffler']");
    private final SelenideElement noUsersTitle = $(byText("There are no users yet"));

    public FriendsPage goToAllPeopleTab() {
        allPeopleTab.click();
        return this;
    }

    public void checkFriendIsVisible(String friendName) {
        friendsList.find(text(friendName)).shouldBe(visible);
    }

    public void checkIncomeFriendshipRequest(String friendName) {
        incomeRequestsList.find(text(friendName)).shouldBe(visible);
    }

    public void checkThereAreNoFriends() {
        friendsList.shouldHave(size(0));
        noUsersTitle.shouldBe(visible);
        lonelyNifflerImg.shouldBe(visible);
    }

    public void checkOutcomeFriendshipRequest(String friendName) {
        allPeopleList.find(text(friendName))
                .shouldBe(visible)
                .$(byText("Waiting..."))
                .shouldBe(visible);
    }

    public void checkFriendPageIsVisible() {
        friendsTab.shouldBe(visible);
        allPeopleTab.shouldBe(visible);
    }

    public void searchFriend(String username) {
        searchField.setValue(username).pressEnter();
    }
}
