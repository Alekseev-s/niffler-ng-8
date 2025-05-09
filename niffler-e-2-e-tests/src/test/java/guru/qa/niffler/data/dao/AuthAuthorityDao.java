package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthAuthorityDao {
    void create(AuthorityEntity... entity);

    Optional<AuthorityEntity> findAuthorityById(UUID id);

    List<AuthorityEntity> findAll();

    void remove(AuthorityEntity authorityEntity);
}
