package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.model.auth.AuthorityValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static guru.qa.niffler.data.template.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();

    @Override
    public AuthUserEntity create(AuthUserEntity userEntity) {
        AuthUserEntity authUserEntity = authUserDao.create(userEntity);
        List<AuthorityEntity> authorityEntities = authUserEntity.getAuthorities();
        authAuthorityDao.create(authorityEntities.toArray(AuthorityEntity[]::new));
        return authUserEntity;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity authUserEntity) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" " +
                        "SET " +
                        "username = ?, " +
                        "password = ?, " +
                        "enabled = ?, " +
                        "account_non_expired = ?, " +
                        "account_non_locked = ?, " +
                        "credentials_non_expired = ? " +
                        "WHERE id = ?"
        )) {
            ps.setString(1, authUserEntity.getUsername());
            ps.setString(2, authUserEntity.getPassword());
            ps.setBoolean(3, authUserEntity.getEnabled());
            ps.setBoolean(4, authUserEntity.getAccountNonExpired());
            ps.setBoolean(5, authUserEntity.getAccountNonLocked());
            ps.setBoolean(6, authUserEntity.getCredentialsNonExpired());
            ps.setObject(7, authUserEntity.getId());
            ps.executeUpdate();
            return authUserEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" u JOIN authority a ON u.id = a.user_id WHERE u.id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity userEntity = null;
                List<AuthorityEntity> authorityEntities = new ArrayList<>();
                while (rs.next()) {
                    if (userEntity == null) {
                        userEntity = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }

                    AuthorityEntity authorityEntity = new AuthorityEntity();
                    authorityEntity.setId(rs.getObject("a.id", UUID.class));
                    authorityEntity.setUser(userEntity);
                    authorityEntity.setAuthority(AuthorityValues.valueOf(rs.getString("authority")));
                    authorityEntities.add(authorityEntity);
                }

                if (userEntity == null) {
                    return Optional.empty();
                } else {
                    userEntity.setAuthorities(authorityEntities);
                    return Optional.of(userEntity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" u JOIN authority a ON u.id = a.user_id WHERE u.username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                AuthUserEntity userEntity = null;
                List<AuthorityEntity> authorityEntities = new ArrayList<>();
                while (rs.next()) {
                    if (userEntity == null) {
                        userEntity = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
                    }

                    AuthorityEntity authorityEntity = new AuthorityEntity();
                    authorityEntity.setUser(userEntity);
                    authorityEntity.setId(rs.getObject("a.id", UUID.class));
                    authorityEntity.setAuthority(AuthorityValues.valueOf(rs.getString("authority")));
                    authorityEntities.add(authorityEntity);
                }
                if (userEntity == null) {
                    return Optional.empty();
                } else {
                    userEntity.setAuthorities(authorityEntities);
                    return Optional.of(userEntity);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
