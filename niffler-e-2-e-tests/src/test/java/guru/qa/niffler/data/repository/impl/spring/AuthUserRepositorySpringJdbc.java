package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.template.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity userEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) VALUES(?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, userEntity.getUsername());
            ps.setString(2, userEntity.getPassword());
            ps.setBoolean(3, userEntity.getEnabled());
            ps.setBoolean(4, userEntity.getAccountNonExpired());
            ps.setBoolean(5, userEntity.getAccountNonLocked());
            ps.setBoolean(6, userEntity.getCredentialsNonExpired());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        userEntity.setId(generatedKey);
        List<AuthorityEntity> authorityEntities = userEntity.getAuthorities();

        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, authorityEntities.get(i).getUser().getId());
                        ps.setString(2, String.valueOf(authorityEntities.get(i).getAuthority()));
                    }

                    @Override
                    public int getBatchSize() {
                        return authorityEntities.size();
                    }
                }
        );

        return userEntity;
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
                                    "u.credentials_non_expired" +
                                    "FROM \"user\" u JOIN authority a ON u.id = a.user_id WHERE u.id = ?",
                            AuthUserEntityExtractor.instance,
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
