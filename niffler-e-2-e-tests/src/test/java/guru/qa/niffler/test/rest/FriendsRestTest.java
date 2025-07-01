package guru.qa.niffler.test.rest;

import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.rest.FriendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.GatewayApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RestTest
public class FriendsRestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @User(
            amountOfFriends = 1,
            amountOfIncomeInvitations = 1
    )
    @ApiLogin
    @Test
    void friendsAndIncomeInvitationsShouldBeReturnedFromGateway(@Token String bearerToken, UserJson user) {
        final UserJson expectedFriend = user.testData().friends().getFirst();
        final UserJson expectedIncomeInvitation = user.testData().incomeInvitations().getFirst();

        final List<UserJson> responseBody = gatewayApiClient.allFriends("Bearer " + bearerToken, null);
        assertEquals(2, responseBody.size());

        UserJson actualIncomeInvitation = responseBody.get(0);
        UserJson actualFriend = responseBody.get(1);

        assertEquals(FriendshipStatus.INVITE_RECEIVED, actualIncomeInvitation.friendshipStatus());
        assertEquals(expectedIncomeInvitation.username(), actualIncomeInvitation.username());

        assertEquals(FriendshipStatus.FRIEND, actualFriend.friendshipStatus());
        assertEquals(expectedFriend.username(), actualFriend.username());
    }

    @User(amountOfFriends = 2)
    @ApiLogin
    @Test
    void friendShouldBeRemovedFromFriendsList(@Token String bearerToken, UserJson user) {
        final UserJson friend = user.testData().friends().get(0);
        final UserJson friendToRemove = user.testData().friends().get(1);

        gatewayApiClient.removeFriend("Bearer " + bearerToken, friendToRemove.username());
        final List<UserJson> friendsList = gatewayApiClient.allFriends("Bearer " + bearerToken, null);

        assertEquals(1, friendsList.size());
        assertEquals(friend.username(), friendsList.getFirst().username());
    }

    @User(amountOfIncomeInvitations = 1)
    @ApiLogin
    @Test
    void friendshipRequestShouldBeAccepted(@Token String bearerToken, UserJson user) {
        final UserJson expectedFriend = user.testData().incomeInvitations().getFirst();
        final FriendJson friendJson = new FriendJson(expectedFriend.username());
        gatewayApiClient.acceptInvitation("Bearer " + bearerToken, friendJson);
        final List<UserJson> friendsList = gatewayApiClient.allFriends("Bearer " + bearerToken, null);
        assertEquals(expectedFriend.username(), friendsList.getFirst().username());
    }

    @User(amountOfIncomeInvitations = 1)
    @ApiLogin
    @Test
    void friendshipRequestShouldBeDeclined(@Token String bearerToken, UserJson user) {
        final UserJson expectedDeclinedFriend = user.testData().incomeInvitations().getFirst();
        final FriendJson friendJson = new FriendJson(expectedDeclinedFriend.username());
        final UserJson actualDeclinedFriend = gatewayApiClient.declineInvitation("Bearer " + bearerToken, friendJson);
        final List<UserJson> friendsList = gatewayApiClient.allFriends("Bearer " + bearerToken, null);
        assertEquals(0, friendsList.size());
        assertNull(actualDeclinedFriend.friendshipStatus());
    }

    @User(amountOfOutcomeInvitations = 1)
    @ApiLogin
    @Test
    void shouldBeOutcomeInvitationAfterFriendshipRequest(@Token String bearerToken, UserJson user) {
        final UserJson expectedInvitationRecipient = user.testData().outcomeInvitations().getFirst();
        final List<UserJson> allUsers = gatewayApiClient.allUsers("Bearer " + bearerToken, null);
        assertEquals(expectedInvitationRecipient.username(), allUsers.getFirst().username());
    }
}
