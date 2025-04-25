package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.model.auth.AuthorityValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.template.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity userEntity) {
        try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) VALUES (?, ?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS
        );
        PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)"
        )) {
            userPs.setString(1, userEntity.getUsername());
            userPs.setString(2, userEntity.getPassword());
            userPs.setBoolean(3, userEntity.getEnabled());
            userPs.setBoolean(4, userEntity.getAccountNonExpired());
            userPs.setBoolean(5, userEntity.getAccountNonLocked());
            userPs.setBoolean(6, userEntity.getCredentialsNonExpired());

            userPs.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }

            userEntity.setId(generatedKey);

            for (AuthorityEntity authorityEntity : userEntity.getAuthorities()) {
                authorityPs.setObject(1, generatedKey);
                authorityPs.setString(2, authorityEntity.getAuthority().name());
                authorityPs.addBatch();
                authorityPs.clearParameters();
            }

            authorityPs.executeBatch();
            return userEntity;
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
}
