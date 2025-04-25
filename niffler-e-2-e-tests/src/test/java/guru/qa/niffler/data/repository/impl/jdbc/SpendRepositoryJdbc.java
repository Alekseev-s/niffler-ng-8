package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;

import java.sql.*;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.template.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        if (spendEntity.getCategory().getId() == null) {
            try (PreparedStatement categoryPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                    "INSERT INTO category (username, name, archived) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                categoryPs.setString(1, spendEntity.getCategory().getUsername());
                categoryPs.setString(2, spendEntity.getCategory().getName());
                categoryPs.setBoolean(3, spendEntity.getCategory().isArchived());
                categoryPs.executeUpdate();

                final UUID categoryGeneratedKey;

                try (ResultSet rs = categoryPs.getGeneratedKeys()) {
                    if (rs.next()) {
                        categoryGeneratedKey = rs.getObject("id", UUID.class);
                        spendEntity.getCategory().setId(categoryGeneratedKey);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        try (PreparedStatement spendPs = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) VALUES ( ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            spendPs.setString(1, spendEntity.getUsername());
            spendPs.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
            spendPs.setString(3, spendEntity.getCurrency().name());
            spendPs.setDouble(4, spendEntity.getAmount());
            spendPs.setString(5, spendEntity.getDescription());
            spendPs.setObject(6, spendEntity.getCategory().getId());
            spendPs.executeUpdate();

            final UUID spendGeneratedKey;

            try (ResultSet rs = spendPs.getGeneratedKeys()) {
                if (rs.next()) {
                    spendGeneratedKey = rs.getObject("id", UUID.class);
                    spendEntity.setId(spendGeneratedKey);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return spendEntity;
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend s JOIN category c ON s.category_id = c.id WHERE s.id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    SpendEntity spendEntity = SpendEntityRowMapper.instance.mapRow(rs, 1);
                    CategoryEntity categoryEntity = spendEntity.getCategory();
                    categoryEntity.setName(rs.getString("name"));
                    categoryEntity.setUsername(rs.getString("username"));
                    categoryEntity.setArchived(rs.getBoolean("archived"));
                    return Optional.of(spendEntity);
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
