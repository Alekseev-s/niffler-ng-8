package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface SpendRepository {

    @Nonnull
    SpendEntity create(SpendEntity spendEntity);

    @Nonnull
    SpendEntity update(SpendEntity spendEntity);

    @Nonnull
    CategoryEntity createCategory(CategoryEntity categoryEntity);

    @Nonnull
    Optional<CategoryEntity> findCategoryById(UUID id);

    @Nonnull
    Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName);

    @Nonnull
    Optional<SpendEntity> findById(UUID id);

    @Nonnull
    Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description);

    void remove(SpendEntity spendEntity);

    void removeCategory(CategoryEntity categoryEntity);
}
