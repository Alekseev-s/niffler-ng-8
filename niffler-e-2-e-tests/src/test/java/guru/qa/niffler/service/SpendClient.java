package guru.qa.niffler.service;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface SpendClient {

    @Nullable
    public SpendJson createSpend(SpendJson spend);

    @Nullable
    public CategoryJson createCategory(CategoryJson categoryJson);
}
