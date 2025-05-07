package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthUserApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit authRetrofit = new Retrofit.Builder()
            .baseUrl(CFG.authUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    private final Retrofit userdataRetrofit = new Retrofit.Builder()
            .baseUrl(CFG.userdataUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final AuthUserApi authUserApi = authRetrofit.create(AuthUserApi.class);
    private final UserdataApi userdataApi = userdataRetrofit.create(UserdataApi.class);

    private final String defaultPassword = "12345";

    @Override
    public UserJson createUser(String username, String password) {
        final Response<UserJson> response;
        try {
            authUserApi.register(username, password, password, null).execute();
            response = userdataApi.currentUser(username).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(201, response.code());
        return response.body();
    }

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
