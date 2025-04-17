package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserDaoSpringJdbc implements AuthUserDao {

    private final DataSource dataSource;

    public AuthUserDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public AuthUserEntity create(AuthUserEntity userEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) VALUES(?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, userEntity.getUsername());
            ps.setString(2, userEntity.getPassword());
            ps.setBoolean(3, userEntity.isEnabled());
            ps.setBoolean(4, userEntity.isAccountNonExpired());
            ps.setBoolean(5, userEntity.isAccountNonLocked());
            ps.setBoolean(6, userEntity.isCredentialsNonExpired());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        userEntity.setId(generatedKey);
        return userEntity;
    }

    @Override
    public Optional<AuthUserEntity> findUserById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        AuthUserEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public List<AuthUserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                AuthUserEntityRowMapper.instance
        );
    }
}
