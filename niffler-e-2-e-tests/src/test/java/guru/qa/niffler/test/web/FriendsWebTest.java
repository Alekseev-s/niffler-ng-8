package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.userdata.StaticUser;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotation.UserType.Type.*;

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

        FriendsPage fiendsPage = Selenide.open(CFG.friendsUrl(), FriendsPage.class);

        fiendsPage.checkFriendPageIsVisible();
        fiendsPage.searchFriend(friendUsername);
        fiendsPage.checkFriendIsVisible(friendUsername);
    }

    @User
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password());

        FriendsPage friendsPage = Selenide.open(CFG.friendsUrl(), FriendsPage.class);

        friendsPage.checkFriendPageIsVisible();
        friendsPage.checkThereAreNoFriends();
    }


    @User(
            amountOfIncomeInvitations = 1
    )
    @Test
    void incomeInvitationShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password());

        String friendUsername = user.testData().incomeInvitations().getFirst().username();

        FriendsPage friendsPage = Selenide.open(CFG.friendsUrl(), FriendsPage.class);

        friendsPage.checkFriendPageIsVisible();
        friendsPage.searchFriend(friendUsername);
        friendsPage.checkIncomeFriendshipRequest(friendUsername);
    }

    @User(
            amountOfOutcomeInvitations = 1
    )
    @Test
    void outcomeInvitationShouldBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.testData().password());

        String friendUsername = user.testData().outcomeInvitations().getFirst().username();

        FriendsPage friendsPage = Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .goToAllPeopleTab();

        friendsPage.checkFriendPageIsVisible();
        friendsPage.searchFriend(friendUsername);
        friendsPage.checkOutcomeFriendshipRequest(friendUsername);
    }
}
