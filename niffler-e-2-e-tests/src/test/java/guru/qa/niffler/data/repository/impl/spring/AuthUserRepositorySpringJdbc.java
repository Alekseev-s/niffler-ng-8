package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.spring.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.template.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

    @Override
    public AuthUserEntity create(AuthUserEntity userEntity) {
        AuthUserEntity authUserEntity = authUserDao.create(userEntity);
        List<AuthorityEntity> authorityEntities = authUserEntity.getAuthorities();
        authAuthorityDao.create(authorityEntities.toArray(AuthorityEntity[]::new));
        return authUserEntity;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity authUserEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE \"user\" " +
                            "SET " +
                            "username = ? " +
                            "currency = ? " +
                            "firstname = ? " +
                            "surname = ? " +
                            "photo = ? " +
                            "photo_small = ? " +
                            "full_name = ? " +
                            "WHERE id = ?"
            );
            ps.setString(1, authUserEntity.getUsername());
            ps.setString(2, authUserEntity.getPassword());
            ps.setBoolean(3, authUserEntity.getEnabled());
            ps.setBoolean(4, authUserEntity.getAccountNonExpired());
            ps.setBoolean(5, authUserEntity.getAccountNonLocked());
            ps.setBoolean(6, authUserEntity.getCredentialsNonExpired());
            ps.setObject(7, authUserEntity.getId());
            return ps;
        });
        return authUserEntity;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.query(
                            "SELECT a.id as authority_id," +
                                    "authority," +
                                    "user_id as id," +
                                    "u.username," +
                                    "u.password," +
                                    "u.enabled," +
                                    "u.account_non_expired," +
                                    "u.account_non_locked," +
                                    "u.credentials_non_expired " +
                                    "FROM \"user\" u JOIN authority a ON u.id = a.user_id WHERE u.id = ?",
                            AuthUserEntityExtractor.instance,
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.query(
                            "SELECT a.id as authority_id," +
                                    "authority," +
                                    "user_id as id," +
                                    "u.username," +
                                    "u.password," +
                                    "u.enabled," +
                                    "u.account_non_expired," +
                                    "u.account_non_locked," +
                                    "u.credentials_non_expired " +
                                    "FROM \"user\" u JOIN authority a ON u.id = a.user_id WHERE u.username = ?",
                            AuthUserEntityExtractor.instance,
                            username
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void remove(AuthUserEntity authUserEntity) {
        for (AuthorityEntity authorityEntity : authUserEntity.getAuthorities()) {
            authAuthorityDao.remove(authorityEntity);
        }
        authUserDao.remove(authUserEntity);
    }
}
