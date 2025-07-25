package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.spend.CurrencyValues;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.template.Connections.holder;

@ParametersAreNonnullByDefault
public class UserdataUserDaoJdbc implements UserdataUserDao {

    private static final Config CFG = Config.getInstance();

    @Nonnull
    @Override
    public UserEntity createUser(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                """
                        INSERT INTO "user" (
                            username,
                            currency,
                            firstname,
                            surname,
                            photo,
                            photo_small,
                            full_name
                        ) VALUES(?, ?, ?, ?, ?, ?, ?)""",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());

            ps.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }

            user.setId(generatedKey);
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(id);
                    userEntity.setUsername(rs.getString("username"));
                    userEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    userEntity.setFirstname(rs.getString("firstname"));
                    userEntity.setSurname(rs.getString("surname"));
                    userEntity.setPhoto(rs.getBytes("photo"));
                    userEntity.setPhotoSmall(rs.getBytes("photo_small"));
                    userEntity.setFullname(rs.getString("full_name"));
                    return Optional.of(userEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(rs.getObject("id", UUID.class));
                    userEntity.setUsername(username);
                    userEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    userEntity.setFirstname(rs.getString("firstname"));
                    userEntity.setSurname(rs.getString("surname"));
                    userEntity.setPhoto(rs.getBytes("photo"));
                    userEntity.setPhotoSmall(rs.getBytes("photo_small"));
                    userEntity.setFullname(rs.getString("full_name"));
                    return Optional.of(userEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public List<UserEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\""
        )) {
            ps.execute();

            List<UserEntity> userEntities = new ArrayList<>();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    UserEntity userEntity = new UserEntity();
                    userEntity.setId(rs.getObject("id", UUID.class));
                    userEntity.setUsername(rs.getString("username"));
                    userEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    userEntity.setFirstname(rs.getString("firstname"));
                    userEntity.setSurname(rs.getString("surname"));
                    userEntity.setPhoto(rs.getBytes("photo"));
                    userEntity.setPhotoSmall(rs.getBytes("photo_small"));
                    userEntity.setFullname(rs.getString("full_name"));
                    userEntities.add(userEntity);
                }
            }

            return userEntities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
