package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class AuthUserEntityRowMapper implements RowMapper<AuthUserEntity> {

    public static final AuthUserEntityRowMapper instance = new AuthUserEntityRowMapper();

    private AuthUserEntityRowMapper() {

    }

    @Nonnull
    @Override
    public AuthUserEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthUserEntity userEntity = new AuthUserEntity();
        userEntity.setId(rs.getObject("id", UUID.class));
        userEntity.setUsername(rs.getString("username"));
        userEntity.setPassword(rs.getString("password"));
        userEntity.setEnabled(rs.getBoolean("enabled"));
        userEntity.setAccountNonExpired(rs.getBoolean("account_non_expired"));
        userEntity.setAccountNonLocked(rs.getBoolean("account_non_locked"));
        userEntity.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        return userEntity;
    }
}
