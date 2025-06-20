package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

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

                        if (userAnno.amountOfIncomeInvitations() > 0) {
                            usersClient.createIncomeInvitations(user, userAnno.amountOfIncomeInvitations());
                        }
                        if (userAnno.amountOfOutcomeInvitations() > 0) {
                            usersClient.createOutcomeInvitations(user, userAnno.amountOfOutcomeInvitations());
                        }
                        if (userAnno.amountOfFriends() > 0) {
                            usersClient.createFriend(user, userAnno.amountOfFriends());
                        }

                        context.getStore(NAMESPACE).put(
                                context.getUniqueId(),
                                user.withPassword(
                                        defaultPassword
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
        final ExtensionContext extensionContext = TestsMethodContextExtension.context();
        return extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), UserJson.class);
     }
}
