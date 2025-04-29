package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
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

    private final CategoryDao categoryDao = new CategoryDaoJdbc();
    private final SpendDao spendDao = new SpendDaoJdbc();

    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = createCategory(spendEntity.getCategory());
            spendEntity.getCategory().setId(categoryEntity.getId());
        }

        return spendDao.create(spendEntity);
    }

    @Override
    public SpendEntity update(SpendEntity spendEntity) {
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = createCategory(spendEntity.getCategory());
            spendEntity.getCategory().setId(categoryEntity.getId());
        }

        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "UPDATE spend " +
                        "SET " +
                        "username = ? " +
                        "spend_date = ? " +
                        "currency = ? " +
                        "amount = ? " +
                        "description = ? " +
                        "category_id = ? " +
                        "WHERE id = ?"
        )) {
            ps.setString(1, spendEntity.getUsername());
            ps.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
            ps.setString(3, spendEntity.getCurrency().name());
            ps.setDouble(4, spendEntity.getAmount());
            ps.setString(5, spendEntity.getDescription());
            ps.setObject(6, spendEntity.getCategory().getId());
            ps.setObject(7, spendEntity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spendEntity;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity categoryEntity) {
        return categoryDao.create(categoryEntity);
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findCategoryById(id);
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName);
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

    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM spend s JOIN category c ON s.category_id = c.id WHERE s.username = ? AND s.description = ?"
        )) {
            ps.setString(1, username);
            ps.setString(2, description);
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

    @Override
    public void remove(SpendEntity spendEntity) {
        spendDao.deleteSpend(spendEntity);
    }

    @Override
    public void removeCategory(CategoryEntity categoryEntity) {
        categoryDao.deleteCategory(categoryEntity);
    }
}
