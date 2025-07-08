package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.ArrayList;
import java.util.List;

import static guru.qa.niffler.jupiter.extension.TestsMethodContextExtension.context;

@ParametersAreNonnullByDefault
public class UserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);

    private final UsersClient usersClient = UsersClient.getInstance();
    private static final String defaultPassword = "12345";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                    if ("".equals(userAnno.username())) {
                        final String username = RandomDataUtils.getRandomUsername();

                        UserJson user = usersClient.createUser(
                                username,
                                defaultPassword
                        );

                        List<UserJson> friends = new ArrayList<>();
                        List<UserJson> income = new ArrayList<>();
                        List<UserJson> outcome = new ArrayList<>();

                        if (userAnno.amountOfIncomeInvitations() > 0) {
                            income = usersClient.createIncomeInvitations(user, userAnno.amountOfIncomeInvitations());
                        }
                        if (userAnno.amountOfOutcomeInvitations() > 0) {
                            outcome = usersClient.createOutcomeInvitations(user, userAnno.amountOfOutcomeInvitations());
                        }
                        if (userAnno.amountOfFriends() > 0) {
                            friends = usersClient.createFriend(user, userAnno.amountOfFriends());
                        }

                        setUser(
                                user.withPassword(
                                        defaultPassword
                                ).withUsers(
                                        friends,
                                        outcome,
                                        income
                                )
                        );
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdUser();
    }

     public static @Nullable UserJson createdUser() {
        final ExtensionContext extensionContext = context();
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserJson.class);
     }

     public static void setUser(UserJson testUser) {
        final ExtensionContext context = context();
        context.getStore(NAMESPACE).put(
                context.getUniqueId(),
                testUser
        );
     }
}
