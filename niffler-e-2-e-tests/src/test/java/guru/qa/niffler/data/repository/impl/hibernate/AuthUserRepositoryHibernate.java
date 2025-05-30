package guru.qa.niffler.data.repository.impl.hibernate;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

@ParametersAreNonnullByDefault
public class AuthUserRepositoryHibernate implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();
    private final EntityManager entityManager = em(CFG.authJdbcUrl());

    @Nonnull
    @Override
    public AuthUserEntity create(AuthUserEntity userEntity) {
        entityManager.joinTransaction();
        entityManager.persist(userEntity);
        return userEntity;
    }

    @Nonnull
    @Override
    public AuthUserEntity update(AuthUserEntity authUserEntity) {
        entityManager.joinTransaction();
        return entityManager.merge(authUserEntity);
    }

    @Nonnull
    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(AuthUserEntity.class, id)
        );
    }

    @Nonnull
    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try {
            return Optional.of(
                    entityManager.createQuery(
                            "SELECT u FROM AuthUserEntity u WHERE u.username =: username",
                                    AuthUserEntity.class)
                            .setParameter("username", username)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void remove(AuthUserEntity authUserEntity) {
        entityManager.joinTransaction();
        AuthUserEntity managed = entityManager.contains(authUserEntity) ? authUserEntity : entityManager.merge(authUserEntity);
        entityManager.remove(managed);
    }
}
