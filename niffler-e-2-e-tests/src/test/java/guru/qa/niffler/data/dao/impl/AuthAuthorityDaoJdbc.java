package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.model.auth.AuthorityValues;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.template.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public void create(AuthorityEntity... authorityEntity) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            for (AuthorityEntity a : authorityEntity) {
                ps.setObject(1, a.getUserId());
                ps.setString(2, String.valueOf(a.getAuthority()));
                ps.addBatch();
                ps.clearParameters();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<AuthorityEntity> findAuthorityById(UUID id) {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM authority WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    AuthorityEntity authorityEntity = new AuthorityEntity();
                    authorityEntity.setId(rs.getObject("id", UUID.class));
                    authorityEntity.setUserId(rs.getObject("user_id", UUID.class));
                    authorityEntity.setAuthority(AuthorityValues.valueOf(rs.getString("authority").toUpperCase()));
                    return Optional.of(authorityEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<AuthorityEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM authority"
        )) {
            ps.execute();

            List<AuthorityEntity> authorityEntities = new ArrayList<>();

            try(ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthorityEntity authorityEntity = new AuthorityEntity();
                    authorityEntity.setId(rs.getObject("id", UUID.class));
                    authorityEntity.setUserId(rs.getObject("user_id", UUID.class));
                    authorityEntity.setAuthority(AuthorityValues.valueOf(rs.getString("authority")));
                    authorityEntities.add(authorityEntity);
                }
            }

            return authorityEntities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
