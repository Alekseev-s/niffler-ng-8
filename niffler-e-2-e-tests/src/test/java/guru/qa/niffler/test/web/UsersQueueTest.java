package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.UserType;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.model.userdata.StaticUser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotation.UserType.Type.*;

@Disabled
@ExtendWith(UsersQueueExtension.class)
public class UsersQueueTest {

    @Test
    void testWithTwoUsers1(@UserType(WITH_FRIEND) StaticUser user) {
        System.out.println(user);
    }

    @Test
    void testWithTwoUsers2(@UserType(EMPTY) StaticUser user) {
        System.out.println(user);
    }

    @Test
    void testWithTwoUsers3(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        System.out.println(user);
    }

    @Test
    void testWithTwoUsers4(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        System.out.println(user);
    }
}
