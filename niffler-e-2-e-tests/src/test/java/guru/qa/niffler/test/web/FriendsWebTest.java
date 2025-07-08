package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.FriendsPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@WebTest
public class FriendsWebTest {

    private static final Config CFG = Config.getInstance();

    @User(
            amountOfFriends = 1
    )
    @ApiLogin
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        String friendUsername = user.testData().friends().getFirst().username();

        Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .checkFriendPageIsVisible()
                .searchFriend(friendUsername)
                .checkFriendIsVisible(friendUsername);
    }

    @User
    @ApiLogin
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .checkFriendPageIsVisible()
                .checkThereAreNoFriends();
    }


    @User(
            amountOfIncomeInvitations = 1
    )
    @ApiLogin
    @Test
    void incomeInvitationShouldBePresentInFriendsTable(UserJson user) {
        String friendUsername = user.testData().incomeInvitations().getFirst().username();

        Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .checkFriendPageIsVisible()
                .searchFriend(friendUsername)
                .checkIncomeFriendshipRequest(friendUsername);
    }

    @User(
            amountOfOutcomeInvitations = 1
    )
    @ApiLogin
    @Test
    void outcomeInvitationShouldBePresentInAllPeoplesTable(UserJson user) {
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
    @ApiLogin
    @Test
    void acceptFriendship(UserJson user) {
        String friendUsername = user.testData().incomeInvitations().getFirst().username();

        Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .acceptFriendship()
                .checkFriendIsVisible(friendUsername);
    }

    @User(
            amountOfIncomeInvitations = 1
    )
    @ApiLogin
    @Test
    void declineFriendship(UserJson user) {
        Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .declineFriendship()
                .checkThereAreNoFriends();
    }
}
