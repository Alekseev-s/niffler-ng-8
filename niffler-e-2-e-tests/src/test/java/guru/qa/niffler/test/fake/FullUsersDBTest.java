package guru.qa.niffler.test.fake;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

@WebTest
@Order(Integer.MAX_VALUE)
public class FullUsersDBTest {

    private final UsersApiClient usersApiClient = new UsersApiClient();

    @User
    @Test
    void fullUsersTest(UserJson user) {
        List<UserJson> allUsers = usersApiClient.getAllUsers(user.username(), null);
        Assertions.assertFalse(allUsers.isEmpty());
    }
}
