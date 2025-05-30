package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class SpendRepositoryHibernate implements SpendRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.spendJdbcUrl());

    @Nonnull
    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        entityManager.joinTransaction();
        CategoryEntity category = spendEntity.getCategory();
        if (category != null) {
            if (category.getId() == null) {
                category = createCategory(category);
            } else {
                category = entityManager.find(CategoryEntity.class, category.getId());
            }
            spendEntity.setCategory(category);
        }
        entityManager.persist(spendEntity);
        return spendEntity;
    }

    @Nonnull
    @Override
    public SpendEntity update(SpendEntity spendEntity) {
        entityManager.joinTransaction();
        return entityManager.merge(spendEntity);
    }

    @Nonnull
    @Override
    public CategoryEntity createCategory(CategoryEntity categoryEntity) {
        entityManager.joinTransaction();
        Optional<CategoryEntity> createdCategory = findCategoryByUsernameAndCategoryName(categoryEntity.getUsername(), categoryEntity.getName());
        if (createdCategory.isEmpty()) {
            entityManager.persist(categoryEntity);
            return categoryEntity;
        } else {
            return createdCategory.get();
        }
    }

    @Nonnull
    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(CategoryEntity.class, id)
        );
    }

    @Nonnull
    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
        try {
            return Optional.of(
                    entityManager.createQuery(
                                    "SELECT c FROM CategoryEntity c WHERE c.username =: username AND c.name =: name",
                                    CategoryEntity.class)
                            .setParameter("username", username)
                            .setParameter("name", name)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(SpendEntity.class, id)
        );
    }

    @Nonnull
    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        try {
            return Optional.of(
                    entityManager.createQuery(
                                    "SELECT s SpendEntity s WHERE s.username =: username AND s.description =: description",
                                    SpendEntity.class)
                            .setParameter("username", username)
                            .setParameter("description", description)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void remove(SpendEntity spendEntity) {
        entityManager.joinTransaction();
        SpendEntity managed = entityManager.contains(spendEntity) ? spendEntity : entityManager.merge(spendEntity);
        entityManager.remove(managed);
    }

    @Override
    public void removeCategory(CategoryEntity categoryEntity) {
        entityManager.joinTransaction();
        CategoryEntity managed = entityManager.contains(categoryEntity) ? categoryEntity : entityManager.merge(categoryEntity);
        entityManager.remove(managed);
    }
}
