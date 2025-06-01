package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.spring.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.spring.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.extractor.SpendEntityExtractor;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.template.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendRepositorySpringJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();
    private final SpendDao spendDao = new SpendDaoSpringJdbc();

    @Nonnull
    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = createCategory(spendEntity.getCategory());
            spendEntity.getCategory().setId(categoryEntity.getId());
        }

        return spendDao.create(spendEntity);
    }

    @Nonnull
    @Override
    public SpendEntity update(SpendEntity spendEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = createCategory(spendEntity.getCategory());
            spendEntity.getCategory().setId(categoryEntity.getId());
        }

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    """
                            UPDATE spend
                            SET
                                username = ?,
                                spend_date = ?,
                                currency = ?,
                                amount = ?,
                                description = ?,
                                category_id = ?
                            WHERE id = ?
                            """
            );
            ps.setString(1, spendEntity.getUsername());
            ps.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
            ps.setString(3, spendEntity.getCurrency().name());
            ps.setDouble(4, spendEntity.getAmount());
            ps.setString(5, spendEntity.getDescription());
            ps.setObject(6, spendEntity.getCategory().getId());
            ps.setObject(7, spendEntity.getId());
            return ps;
        });
        return spendEntity;
    }

    @Nonnull
    @Override
    public CategoryEntity createCategory(CategoryEntity categoryEntity) {
        return categoryDao.create(categoryEntity);
    }

    @Nonnull
    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return categoryDao.findCategoryById(id);
    }

    @Nonnull
    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName);
    }

    @Nonnull
    @Override
    public Optional<SpendEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.query(
                            "SELECT * FROM spend s JOIN category c ON s.category_id = c.id WHERE s.id = ?",
                            SpendEntityExtractor.instance,
                            id
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        try {
            return Optional.ofNullable(
                    jdbcTemplate.query(
                            "SELECT * FROM spend s JOIN category c ON s.category_id = c.id WHERE s.username = ? AND s.description = ?",
                            SpendEntityExtractor.instance,
                            username,
                            description
                    )
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
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
