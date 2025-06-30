package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthUserApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStorage;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.api.core.RestClient.EmptyRestClient;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.INVITE_RECEIVED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.INVITE_SENT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private final String defaultPassword = "12345";

    private final AuthUserApi authUserApi = new EmptyRestClient(
            CFG.authUrl(),
            new AllureOkHttp3()
                    .setRequestTemplate("http-request.ftl")
                    .setResponseTemplate("http-response.ftl"))
            .create(AuthUserApi.class);
    private final UserdataApi userdataApi = new EmptyRestClient(
            CFG.userdataUrl(),
            new AllureOkHttp3()
                    .setRequestTemplate("http-request.ftl")
                    .setResponseTemplate("http-response.ftl"))
            .create(UserdataApi.class);

    @Step("Create user")
    @Nonnull
    @Override
    public UserJson createUser(String username, String password) {
        final Response<UserJson> response;
        try {
            authUserApi.requestRegisterForm().execute();
            authUserApi.register(
                            username,
                            password,
                            password,
                            ThreadSafeCookieStorage.INSTANCE.cookieValue("XSRF-TOKEN"))
                    .execute();
            response = userdataApi.currentUser(username).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, response.code());
        return response.body();
    }

    @Step("Get current user")
    public UserJson currentUser(String username){
        final Response<UserJson> response;
        try {
            response = userdataApi.currentUser(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Create income invitation")
    @Override
    public List<UserJson> createIncomeInvitations(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = RandomDataUtils.getRandomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, defaultPassword);
                    response = userdataApi.sendInvitation(username, targetUser.username()).execute();
                    result.add(newUser);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());
            }
        }
        return result;
    }

    @Step("Create outcome invitation")
    @Override
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = RandomDataUtils.getRandomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, defaultPassword);
                    response = userdataApi.sendInvitation(targetUser.username(), username).execute();
                    result.add(newUser);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());
            }
        }
        return result;
    }

    @Step("Create friendship")
    @Override
    public List<UserJson> createFriend(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = RandomDataUtils.getRandomUsername();
                final UserJson newUser;
                final Response<UserJson> invitationResponse;
                final Response<UserJson> acceptResponse;
                try {
                    newUser = createUser(username, defaultPassword);
                    invitationResponse = userdataApi.sendInvitation(username, targetUser.username()).execute();
                    acceptResponse = userdataApi.acceptInvitation(targetUser.username(), username).execute();
                    result.add(newUser);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, invitationResponse.code());
                assertEquals(200, acceptResponse.code());
            }
        }
        return result;
    }

    @Step("Get all users")
    @Nonnull
    public List<UserJson> getAllUsers(String username, @Nullable String searchQuery) {
        final Response<List<UserJson>> response;
        try {
            response = userdataApi.allUsers(username, searchQuery).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
        return response.body() != null ? response.body() : Collections.emptyList();
    }

    @Step("Get friends")
    public List<UserJson> getFriends(String username, @Nullable String searchQuery){
        final Response<List<UserJson>> response;
        try {
            response = userdataApi.friends(username, searchQuery).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body() != null ? response.body() : Collections.emptyList();
    }

    @Step("Get income invitations")
    public List<UserJson> getIncomeInvitations(String username, @Nullable String searchQuery){
        List<UserJson> friends = getFriends(username, searchQuery);

        return friends.stream()
                .filter(userJson -> userJson.friendshipStatus().equals(INVITE_RECEIVED))
                .toList();
    }

    @Step("Get outcome invitations")
    @Nonnull
    public List<UserJson> getOutcomeInvitations(String username, @Nullable String searchQuery){
        List<UserJson> allPeople = getAllUsers(username, searchQuery);

        return allPeople.stream()
                .filter(userJson -> userJson.friendshipStatus().equals(INVITE_SENT))
                .collect(Collectors.toList());
    }
}
