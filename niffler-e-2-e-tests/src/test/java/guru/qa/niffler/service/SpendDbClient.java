package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

public class SpendDbClient {

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    public SpendJson createSpend(SpendJson spendJson) {
        SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
        CategoryEntity inputCategory = spendEntity.getCategory();
        CategoryEntity finalCategory = categoryDao.findCategoryByUsernameAndCategoryName(
                inputCategory.getUsername(),
                inputCategory.getName()
        ).orElseGet(() -> categoryDao.create(inputCategory));
        spendEntity.setCategory(finalCategory);
        return SpendJson.fromEntity(spendDao.create(spendEntity));
    }

    public CategoryJson createCategory(CategoryJson categoryJson) {
        CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
        return CategoryJson.fromEntity(categoryDao.create(categoryEntity));
    }
}
