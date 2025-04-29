package guru.qa.niffler.service;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

public interface SpendClient {

    public SpendJson createSpend(SpendJson spend);

    public CategoryJson createCategory(CategoryJson categoryJson);
}
