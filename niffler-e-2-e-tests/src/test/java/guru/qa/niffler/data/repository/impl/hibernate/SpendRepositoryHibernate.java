package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class SpendRepositoryHibernate implements SpendRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.spendJdbcUrl());

    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        entityManager.joinTransaction();
        entityManager.persist(spendEntity);
        return spendEntity;
    }

    @Override
    public SpendEntity update(SpendEntity spendEntity) {
        entityManager.joinTransaction();
        return entityManager.merge(spendEntity);
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity categoryEntity) {
        entityManager.joinTransaction();
        entityManager.persist(categoryEntity);
        return categoryEntity;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(CategoryEntity.class, id)
        );
    }

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

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(SpendEntity.class, id)
        );
    }

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
