package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OauthTest {

    private final AuthApiClient authApiClient = new AuthApiClient();

    @User
    @Test
    void oauthTest(UserJson user) {
        String token = authApiClient.login(user.username(), user.testData().password());
        assertNotNull(token);
    }
}
