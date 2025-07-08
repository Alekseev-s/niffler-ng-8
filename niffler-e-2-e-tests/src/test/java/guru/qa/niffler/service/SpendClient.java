package guru.qa.niffler.service;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.SpendDbClient;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SpendClient {

    static SpendClient getInstance() {
        return "api".equals(System.getProperty("client.impl"))
                ? new SpendApiClient()
                : new SpendDbClient();
    }

    @Nullable
    public SpendJson createSpend(SpendJson spend);

    @Nullable
    public CategoryJson createCategory(CategoryJson categoryJson);
}
