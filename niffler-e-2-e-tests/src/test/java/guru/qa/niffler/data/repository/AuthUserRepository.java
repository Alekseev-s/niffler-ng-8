package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {

    AuthUserEntity create(AuthUserEntity userEntity);

    AuthUserEntity update(AuthUserEntity authUserEntity);

    Optional<AuthUserEntity> findById(UUID id);

    Optional<AuthUserEntity> findByUsername(String username);

    void remove(AuthUserEntity authUserEntity);
}
