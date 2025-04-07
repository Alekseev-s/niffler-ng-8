package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.model.StaticUser;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.annotation.UserType.Type.*;

@WebTest
public class FriendsWebTest {

    private static final Config CFG = Config.getInstance();

    @Test
    void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password());

        FriendsPage fiendsPage = Selenide.open(CFG.friendsUrl(), FriendsPage.class);

        fiendsPage.checkFriendPageIsVisible();
        fiendsPage.checkFriendIsVisible(user.friend());
    }

    @Test
    void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password());

        FriendsPage friendsPage = Selenide.open(CFG.friendsUrl(), FriendsPage.class);

        friendsPage.checkFriendPageIsVisible();
        friendsPage.checkThereAreNoFriends();
    }

    @Test
    void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password());

        FriendsPage friendsPage = Selenide.open(CFG.friendsUrl(), FriendsPage.class);

        friendsPage.checkFriendPageIsVisible();
        friendsPage.checkIncomeFriendshipRequest(user.income());
    }

    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin(user.username(), user.password());

        FriendsPage friendsPage = Selenide.open(CFG.friendsUrl(), FriendsPage.class)
                .goToAllPeopleTab();

        friendsPage.checkFriendPageIsVisible();
        friendsPage.checkOutcomeFriendshipRequest(user.outcome());
    }
}
