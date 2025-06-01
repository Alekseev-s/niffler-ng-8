package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.extension.UserClientExtension;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(UserClientExtension.class)
public class JdbcTest {

    private UsersClient usersClient;

    @Test
    void createUser() {
        UserJson user = usersClient.createUser(
                RandomDataUtils.getRandomUsername(),
                "12345"
        );

        System.out.println(user);
    }

    @Test
    void createIncomeInvitation() {
        UserJson user = usersClient.createUser(
                RandomDataUtils.getRandomUsername(),
                "12345"
        );

        usersClient.createIncomeInvitations(user, 1);
    }

    @Test
    void createOutcomeInvitation() {
        UserJson user = usersClient.createUser(
                RandomDataUtils.getRandomUsername(),
                "12345"
        );

        usersClient.createOutcomeInvitations(user, 1);
    }

    @Test
    void createFriendship() {
        UserJson user = usersClient.createUser(
                RandomDataUtils.getRandomUsername(),
                "12345"
        );

        usersClient.createFriend(user, 1);
    }
}

