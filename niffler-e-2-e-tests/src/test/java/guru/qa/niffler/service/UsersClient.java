package guru.qa.niffler.service;

import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.service.impl.UsersDbClient;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public interface UsersClient {

    static UsersClient getInstance() {
        return "api".equals(System.getProperty("client.impl"))
                ? new UsersApiClient()
                : new UsersDbClient();
    }

    @Nonnull
    UserJson createUser(String username, String password);

    List<UserJson> createIncomeInvitations(UserJson targetUser, int count);

    List<UserJson> createOutcomeInvitations(UserJson targetUser, int count);

    List<UserJson> createFriend(UserJson targetUser, int count);
}
