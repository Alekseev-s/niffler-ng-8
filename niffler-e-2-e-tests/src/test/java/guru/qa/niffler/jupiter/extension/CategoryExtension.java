package guru.qa.niffler.jupiter.extension;

import com.github.javafaker.Faker;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements BeforeEachCallback, ParameterResolver, AfterTestExecutionCallback {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final Faker faker = new Faker();

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                    CategoryJson category = new CategoryJson(
                            null,
                            faker.commerce().productName(),
                            anno.username(),
                            false
                    );

                    CategoryJson created = spendApiClient.addCategory(category);
                    if (anno.archived()) {
                        CategoryJson archivedCategory = new CategoryJson(
                                created.id(),
                                created.name(),
                                created.username(),
                                true
                        );
                        created = spendApiClient.updateCategory(archivedCategory);
                    }
                    context.getStore(NAMESPACE).put(context.getUniqueId(), created);
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(CategoryExtension.NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        CategoryJson category = context.getStore(CategoryExtension.NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        if (!category.archived()) {
            CategoryJson archivedCategory = new CategoryJson(
                    category.id(),
                    category.name(),
                    category.username(),
                    true
            );
            spendApiClient.updateCategory(archivedCategory);
        }
    }
}
