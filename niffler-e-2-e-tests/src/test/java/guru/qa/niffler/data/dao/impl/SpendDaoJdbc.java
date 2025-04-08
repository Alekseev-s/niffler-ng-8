package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.SpendEntity;

import java.sql.*;
import java.util.UUID;

public class SpendDaoJdbc implements SpendDao {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
            try (PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) VALUES ( ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            )) {
                ps.setString(1, spendEntity.getUsername());
                ps.setDate(2, spendEntity.getSpendDate());
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

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
