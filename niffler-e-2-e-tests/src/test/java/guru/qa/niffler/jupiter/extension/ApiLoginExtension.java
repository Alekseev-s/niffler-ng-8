package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.core.ThreadSafeCookieStorage;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.model.userdata.TestData;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import javax.annotation.ParametersAreNonnullByDefault;

import static guru.qa.niffler.jupiter.extension.TestsMethodContextExtension.context;

@ParametersAreNonnullByDefault
public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {

    private static final Config CFG = Config.getInstance();
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);

    private final AuthApiClient authApiClient = new AuthApiClient();
    private final boolean setupBrowser;

    private ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    public ApiLoginExtension() {
        this.setupBrowser = true;
    }

    public static ApiLoginExtension restApiLoginExtension() {
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {
                    final UserJson userToLogin;
                    final UserJson userFromUserExtension = UserExtension.createdUser();
                    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
                        if (userFromUserExtension == null) {
                            throw new IllegalStateException("@User must be present in case that @ApiLogin is empty!");
                        }
                        userToLogin = userFromUserExtension;
                    } else {
                        UserJson fakeUser = new UserJson(
                                apiLogin.username(),
                                new TestData(
                                        apiLogin.password()
                                )
                        );
                        if (userFromUserExtension != null) {
                            throw new IllegalStateException("@User must not be present in case that @ApiLogin contains username or password!");
                        }
                        UserExtension.setUser(fakeUser);
                        userToLogin = fakeUser;
                    }

                    final String token = authApiClient.
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return false;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return null;
    }

    public static void setToken(String token) {
        context().getStore(NAMESPACE).put("token", token);
    }

    public static String getToken() {
        return context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setCode(String code) {
        context().getStore(NAMESPACE).put("code", code);
    }

    public static String getCode() {
        return context().getStore(NAMESPACE).get("code", String.class);
    }

    public static Cookie getJsessionIdCookie() {
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStorage.INSTANCE.cookieValue("JSESSIONID")
        );
    }
}
