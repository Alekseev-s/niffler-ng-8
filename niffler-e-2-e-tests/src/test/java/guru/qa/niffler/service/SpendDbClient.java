package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    public SpendJson createSpend(SpendJson spendJson) {
        return transaction(connection -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            CategoryEntity inputCategory = spendEntity.getCategory();
            if (inputCategory.getId() == null) {
                CategoryEntity finalCategory = new CategoryDaoJdbc(connection).findCategoryByUsernameAndCategoryName(
                        inputCategory.getUsername(),
                        inputCategory.getName()
                ).orElseGet(() -> new CategoryDaoJdbc(connection).create(inputCategory));
                spendEntity.setCategory(finalCategory);
            }
            return SpendJson.fromEntity(new SpendDaoJdbc(connection).create(spendEntity));
        }, CFG.spendJdbcUrl(), 1);
    }

    public CategoryJson createCategory(CategoryJson categoryJson) {
        return transaction(connection -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).create(categoryEntity));
        }, CFG.spendJdbcUrl(), 1);
    }
}
