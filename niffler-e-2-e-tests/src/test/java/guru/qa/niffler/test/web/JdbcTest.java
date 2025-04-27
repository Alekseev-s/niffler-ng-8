package guru.qa.niffler.test.web;

import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class JdbcTest {

    static UserDbClient userDbClient = new UserDbClient();

    @ValueSource(strings = {
            "valentine-3"
    })
    @ParameterizedTest
    void springJdbcTest(String username) {
        UserJson userJson = userDbClient.createUser(
                username,
                "12345"
        );

        userDbClient.addIncomeInvitation(userJson, 1);
        userDbClient.addOutcomeInvitation(userJson, 1);
    }
}
