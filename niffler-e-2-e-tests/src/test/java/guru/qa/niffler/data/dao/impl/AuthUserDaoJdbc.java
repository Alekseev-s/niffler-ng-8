package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.AuthUserEntity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoJdbc implements AuthUserDao {

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final Connection connection;

    public AuthUserDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthUserEntity create(AuthUserEntity userEntity) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) VALUES (?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, userEntity.getUsername());
            ps.setString(2, pe.encode(userEntity.getPassword()));
            ps.setBoolean(3, userEntity.isEnabled());
            ps.setBoolean(4, userEntity.isAccountNonExpired());
            ps.setBoolean(5, userEntity.isAccountNonLocked());
            ps.setBoolean(6, userEntity.isCredentialsNonExpired());

            ps.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }

            userEntity.setId(generatedKey);
            return userEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthUserEntity> findUserById(UUID id) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthUserEntity userEntity = new AuthUserEntity();
                    userEntity.setId(rs.getObject("id", UUID.class));
                    userEntity.setUsername(rs.getString("username"));
                    userEntity.setPassword(rs.getString("password"));
                    userEntity.setEnabled(rs.getBoolean("enabled"));
                    userEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    userEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    userEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    return Optional.of(userEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
