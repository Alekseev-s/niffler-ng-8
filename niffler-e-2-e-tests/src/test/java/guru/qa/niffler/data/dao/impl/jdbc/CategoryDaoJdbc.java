package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.template.Connections.holder;

@ParametersAreNonnullByDefault
public class CategoryDaoJdbc implements CategoryDao {

    private static final Config CFG = Config.getInstance();

    @Nonnull
    @Override
    public CategoryEntity create(CategoryEntity categoryEntity) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO category (username, name, archived) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, categoryEntity.getUsername());
            ps.setString(2, categoryEntity.getName());
            ps.setBoolean(3, categoryEntity.isArchived());

            ps.executeUpdate();

            final UUID generatedKey;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }

            categoryEntity.setId(generatedKey);
            return categoryEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM category WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getObject("id", UUID.class));
                    categoryEntity.setUsername(rs.getString("username"));
                    categoryEntity.setName(rs.getString("name"));
                    categoryEntity.setArchived(rs.getBoolean("archived"));
                    return Optional.of(categoryEntity);
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
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM category WHERE name = ? AND username = ?"
        )) {
            ps.setString(1, categoryName);
            ps.setString(2, username);
            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getObject("id", UUID.class));
                    categoryEntity.setName(rs.getString("name"));
                    categoryEntity.setUsername(rs.getString("username"));
                    categoryEntity.setArchived(rs.getBoolean("archived"));
                    return Optional.of(categoryEntity);
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
    public List<CategoryEntity> findAllByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM category WHERE username = ?"
        )) {
            ps.setString(1, username);
            ps.execute();

            List<CategoryEntity> categoryEntities = new ArrayList<>();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getObject("id", UUID.class));
                    categoryEntity.setName(rs.getString("name"));
                    categoryEntity.setUsername(rs.getString("username"));
                    categoryEntity.setArchived(rs.getBoolean("archived"));
                    categoryEntities.add(categoryEntity);
                }
            }
            return categoryEntities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    @Override
    public List<CategoryEntity> findAll() {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM category"
        )) {
            ps.execute();

            List<CategoryEntity> categoryEntities = new ArrayList<>();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    CategoryEntity categoryEntity = new CategoryEntity();
                    categoryEntity.setId(rs.getObject("id", UUID.class));
                    categoryEntity.setName(rs.getString("name"));
                    categoryEntity.setUsername(rs.getString("username"));
                    categoryEntity.setArchived(rs.getBoolean("archived"));
                    categoryEntities.add(categoryEntity);
                }
            }

            return categoryEntities;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCategory(CategoryEntity categoryEntity) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM category WHERE id = ?"
        )) {
            ps.setObject(1, categoryEntity.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
