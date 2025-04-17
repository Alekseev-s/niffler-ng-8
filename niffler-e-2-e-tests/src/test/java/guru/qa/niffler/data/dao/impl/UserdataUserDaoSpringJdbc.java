package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserDaoSpringJdbc implements UserdataUserDao {

    private DataSource dataSource;

    public UserdataUserDaoSpringJdbc(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) VALUES (?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, userEntity.getUsername());
            ps.setString(2, userEntity.getCurrency().name());
            ps.setString(3, userEntity.getFirstname());
            ps.setString(4, userEntity.getSurname());
            ps.setBytes(5, userEntity.getPhoto());
            ps.setBytes(6, userEntity.getPhotoSmall());
            ps.setString(7, userEntity.getFullname());
            return ps;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        userEntity.setId(generatedKey);
        return userEntity;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        UserdataUserEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE username = ?",
                        UserdataUserEntityRowMapper.instance,
                        username
                )
        );
    }

    @Override
    public List<UserEntity> findAll() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                UserdataUserEntityRowMapper.instance
        );
    }

    @Override
    public void delete(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(
                "DELETE FROM \"user\" WHERE id = ?",
                user.getId()
        );
    }
}
