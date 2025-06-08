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
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private final String defaultPassword = "12345";

    private final AuthUserApi authUserApi = new EmptyRestClient(CFG.authUrl()).create(AuthUserApi.class);
    private final UserdataApi userdataApi = new EmptyRestClient(CFG.userdataUrl()).create(UserdataApi.class);

    @Step("Create user")
    @Nonnull
    @Override
    public UserJson createUser(String username, String password) {
        final Response<UserJson> response;
        try {
            authUserApi.getRegisterForm().execute();
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

    @Step("Create income invitation")
    @Override
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String username = RandomDataUtils.getRandomUsername();
                final Response<UserJson> response;
                createUser(username, defaultPassword);
                try {
                    response = userdataApi.sendInvitation(username, targetUser.username()).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());
            }
        }
    }

    @Step("Create outcome invitation")
    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String username = RandomDataUtils.getRandomUsername();
                final Response<UserJson> response;
                createUser(username, defaultPassword);
                try {
                    response = userdataApi.sendInvitation(targetUser.username(), username).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());
            }
        }
    }

    @Step("Create friendship")
    @Override
    public void createFriend(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                String username = RandomDataUtils.getRandomUsername();
                final Response<UserJson> invitationResponse;
                final Response<UserJson> acceptResponse;
                createUser(username, defaultPassword);
                try {
                    invitationResponse = userdataApi.sendInvitation(username, targetUser.username()).execute();
                    acceptResponse = userdataApi.acceptInvitation(targetUser.username(), username).execute();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, invitationResponse.code());
                assertEquals(200, acceptResponse.code());
            }
        }
    }
}
