package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OauthTest {

    private final UsersApiClient usersClient = new UsersApiClient();

    @User
    @Test
    void oauthTest(UserJson user) {
        String token = usersClient.login(user.username(), user.testData().password());
        assertNotNull(token);
    }
}
