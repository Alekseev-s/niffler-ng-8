package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.model.auth.AuthorityValues;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthUserEntityExtractor implements ResultSetExtractor<AuthUserEntity> {

    public static final AuthUserEntityExtractor instance = new AuthUserEntityExtractor();

    private AuthUserEntityExtractor() {

    }

    @Override
    public AuthUserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, AuthUserEntity> userMap = new ConcurrentHashMap<>();
        UUID userId = null;
        while (rs.next()) {
            userId = rs.getObject("id", UUID.class);
            AuthUserEntity userEntity = userMap.computeIfAbsent(userId, id -> {
                AuthUserEntity user = new AuthUserEntity();
                try {
                    user.setId(id);
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEnabled(rs.getBoolean("enabled"));
                    user.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    user.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    user.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return user;
            });

            AuthorityEntity authorityEntity = new AuthorityEntity();
            authorityEntity.setId(rs.getObject("authority_id", UUID.class));
            authorityEntity.setAuthority(AuthorityValues.valueOf(rs.getString("authority")));
            userEntity.getAuthorities().add(authorityEntity);
        }
        return userMap.get(userId);
    }
}
