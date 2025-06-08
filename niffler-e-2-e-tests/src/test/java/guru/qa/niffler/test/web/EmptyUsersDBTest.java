package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

@Order(1)
public class EmptyUsersDBTest {

    private final UsersApiClient usersApiClient = new UsersApiClient();

    @User
    @Test
    void emptyUsersTest(UserJson user) {
        List<UserJson> users = usersApiClient.getAllUsers(user.username(), null);
        Assertions.assertTrue(users.isEmpty());
    }
}
