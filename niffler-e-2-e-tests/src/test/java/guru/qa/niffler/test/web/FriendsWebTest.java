package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class FriendsWebTest {

    private static final Config CFG = Config.getInstance();

    @User(
            amountOfFriends = 1
    )
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password());

        String friendUsername = user.testData().friends().getFirst().username();

        Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .checkFriendPageIsVisible()
                .searchFriend(friendUsername)
                .checkFriendIsVisible(friendUsername);
    }

    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password());

        Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .checkFriendPageIsVisible()
                .checkThereAreNoFriends();
    }


    @User(
            amountOfIncomeInvitations = 1
    )
    @Test
    void incomeInvitationShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password());

        String friendUsername = user.testData().incomeInvitations().getFirst().username();

        Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .checkFriendPageIsVisible()
                .searchFriend(friendUsername)
                .checkIncomeFriendshipRequest(friendUsername);
    }

    @User(
            amountOfOutcomeInvitations = 1
    )
    @Test
    void outcomeInvitationShouldBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password());

        String friendUsername = user.testData().outcomeInvitations().getFirst().username();

        Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .goToAllPeopleTab()
                .checkFriendPageIsVisible()
                .searchFriend(friendUsername)
                .checkOutcomeFriendshipRequest(friendUsername);
    }

    @User(
            amountOfIncomeInvitations = 1
    )
    @Test
    void acceptFriendship(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password());

        String friendUsername = user.testData().incomeInvitations().getFirst().username();

        Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .acceptFriendship()
                .checkFriendIsVisible(friendUsername);
    }

    @User(
            amountOfIncomeInvitations = 1
    )
    @Test
    void declineFriendship(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password());
        Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .declineFriendship()
                .checkThereAreNoFriends();
    }
}
