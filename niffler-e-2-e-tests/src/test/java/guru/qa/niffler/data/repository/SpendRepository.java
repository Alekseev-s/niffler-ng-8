package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {

    SpendEntity create(SpendEntity spendEntity);

    SpendEntity update(SpendEntity spendEntity);

    CategoryEntity createCategory(CategoryEntity categoryEntity);

    Optional<CategoryEntity> findCategoryById(UUID id);

    Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName);

    Optional<SpendEntity> findById(UUID id);

    Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description);

    void remove(SpendEntity spendEntity);

    void removeCategory(CategoryEntity categoryEntity);
}
