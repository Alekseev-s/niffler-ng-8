package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.core.ThreadSafeCookieStorage;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class CookiesExtension implements AfterTestExecutionCallback {
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        ThreadSafeCookieStorage.INSTANCE.removeAll();
    }
}
