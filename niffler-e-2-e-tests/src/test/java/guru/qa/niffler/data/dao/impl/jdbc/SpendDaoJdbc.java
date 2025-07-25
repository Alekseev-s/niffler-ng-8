package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
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
public class SpendDaoJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();

    @Nonnull
    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                """
                        INSERT INTO spend (
                            username,
                            spend_date,
                            currency,
                            amount,
                            description,
                            category_id
                        ) VALUES ( ?, ?, ?, ?, ?, ?)""",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spendEntity.getUsername());
            ps.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
            ps.setString(3, spendEntity.getCurrency().name());
            ps.setDouble(4, spendEntity.getAmount());
            ps.setString(5, spendEntity.getDescription());
            ps.setObject(6, spendEntity.getCategory().getId());

            ps.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }

            spendEntity.setId(generatedKey);
            return spendEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public Optional<SpendEntity> findSpendById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity spendEntity = new SpendEntity();
                    spendEntity.setId(rs.getObject("id", UUID.class));
                    spendEntity.setUsername(rs.getString("username"));
                    spendEntity.setSpendDate(rs.getDate("spend_date"));
                    spendEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    spendEntity.setAmount(rs.getDouble("amount"));
                    spendEntity.setDescription(rs.getString("description"));
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getObject("category_id", UUID.class));
                    spendEntity.setCategory(categoryEntity);
                    return Optional.of(spendEntity);
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
    public List<SpendEntity> findAllByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend WHERE username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();

            List<SpendEntity> spendList = new ArrayList<>();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    SpendEntity spendEntity = new SpendEntity();
                    spendEntity.setId(rs.getObject("id", UUID.class));
                    spendEntity.setUsername(rs.getString("username"));
                    spendEntity.setSpendDate(rs.getDate("spend_date"));
                    spendEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    spendEntity.setAmount(rs.getDouble("amount"));
                    spendEntity.setDescription(rs.getString("description"));
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getObject("category_id", UUID.class));
                    spendEntity.setCategory(categoryEntity);
                    spendList.add(spendEntity);
                }
            }

            return spendList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public List<SpendEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend"
        )) {
            ps.execute();

            List<SpendEntity> spendList = new ArrayList<>();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    SpendEntity spendEntity = new SpendEntity();
                    spendEntity.setId(rs.getObject("id", UUID.class));
                    spendEntity.setUsername(rs.getString("username"));
                    spendEntity.setSpendDate(rs.getDate("spend_date"));
                    spendEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    spendEntity.setAmount(rs.getDouble("amount"));
                    spendEntity.setDescription(rs.getString("description"));
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getObject("category_id", UUID.class));
                    spendEntity.setCategory(categoryEntity);
                    spendList.add(spendEntity);
                }
            }

            return spendList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteSpend(SpendEntity spendEntity) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, spendEntity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
