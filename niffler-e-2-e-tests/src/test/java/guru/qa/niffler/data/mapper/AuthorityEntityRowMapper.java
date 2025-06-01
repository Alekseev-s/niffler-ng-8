package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.model.auth.AuthorityValues;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Nonnull;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthorityEntityRowMapper implements RowMapper<AuthorityEntity> {

    public static final AuthorityEntityRowMapper instance = new AuthorityEntityRowMapper();

    private AuthorityEntityRowMapper() {

    }

    @Nonnull
    @Override
    public AuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        AuthorityEntity authorityEntity = new AuthorityEntity();
        authorityEntity.setId(rs.getObject("id", UUID.class));
        authorityEntity.setAuthority(AuthorityValues.valueOf(rs.getString("authority")));
        return authorityEntity;
    }
}
