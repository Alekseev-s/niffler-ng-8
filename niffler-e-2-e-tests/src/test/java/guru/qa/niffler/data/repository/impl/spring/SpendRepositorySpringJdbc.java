package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.extractor.SpendEntityExtractor;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.template.DataSources;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendEntity create(SpendEntity spendEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();

        if (spendEntity.getCategory().getId() == null) {
            jdbcTemplate.update(
                    con -> {
                        PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO category (username, name, archived) VALUES (?, ?, ?)",
                                Statement.RETURN_GENERATED_KEYS
                        );
                        ps.setString(1, spendEntity.getCategory().getUsername());
                        ps.setString(2, spendEntity.getCategory().getName());
                        ps.setBoolean(3, spendEntity.getCategory().isArchived());

                        return ps;
                    }, kh);

            final UUID generatedKey = (UUID) kh.getKeys().get("id");
            spendEntity.getCategory().setId(generatedKey);
        }

        jdbcTemplate.update(
                con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) VALUES ( ?, ?, ?, ?, ?, ?)",
                            Statement.RETURN_GENERATED_KEYS
                    );
                    ps.setString(1, spendEntity.getUsername());
                    ps.setDate(2, new Date(spendEntity.getSpendDate().getTime()));
                    ps.setString(3, spendEntity.getCurrency().name());
                    ps.setDouble(4, spendEntity.getAmount());
                    ps.setString(5, spendEntity.getDescription());
                    ps.setObject(6, spendEntity.getCategory().getId());

                    return ps;
                }, kh);
        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        spendEntity.setId(generatedKey);
        return spendEntity;
    }

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
}
