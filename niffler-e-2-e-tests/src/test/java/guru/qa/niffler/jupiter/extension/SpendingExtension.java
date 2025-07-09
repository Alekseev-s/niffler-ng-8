package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendDbClient;
import org.apache.commons.lang.ArrayUtils;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
    private final SpendClient spendClient = new SpendDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(anno -> {
                    if (ArrayUtils.isNotEmpty(anno.spends())) {
                        UserJson createdUser = UserExtension.createdUser();
                        final String username = createdUser != null
                                ? createdUser.username()
                                : anno.username();

                        final List<CategoryJson> existingCategories = createdUser != null
                                ? createdUser.testData().categories()
                                : CategoryExtension.createdCategories(context);

                        final List<SpendJson> createdSpendings = new ArrayList<>();

                        for (Spending spendAnno : anno.spends()) {
                            final Optional<CategoryJson> matchedCategory = existingCategories.stream()
                                    .filter(category -> category.name().equals(spendAnno.category()))
                                    .findFirst();

                            SpendJson spend = new SpendJson(
                                    null,
                                    new Date(),
                                    matchedCategory.orElseGet(() -> new CategoryJson(
                                            null,
                                            spendAnno.category(),
                                            username,
                                            false
                                    )),
                                    spendAnno.currency(),
                                    spendAnno.amount(),
                                    spendAnno.description(),
                                    username
                            );

                            createdSpendings.add(spendClient.createSpend(spend));
                        }

                        if (createdUser != null) {
                            createdUser.testData().spendings().addAll(createdSpendings);
                        } else {
                            context.getStore(NAMESPACE).put(
                                    context.getUniqueId(),
                                    createdSpendings
                            );
                        }
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
    }

    @Override
    public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return createdSpendings(extensionContext).toArray(SpendJson[]::new);
    }

    @SuppressWarnings("unchecked")
    public static List<SpendJson> createdSpendings(ExtensionContext extensionContext) {
        return Optional.ofNullable(extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class))
                .orElse(Collections.emptyList());
    }
}
