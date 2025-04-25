package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.spend.CurrencyValues;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpendEntityExtractor implements ResultSetExtractor<SpendEntity> {

    public static final SpendEntityExtractor instance = new SpendEntityExtractor();

    private SpendEntityExtractor() {

    }

    @Override
    public SpendEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        SpendEntity spendEntity = new SpendEntity();
        if (rs.next()) {
            spendEntity.setId(rs.getObject("id", UUID.class));
            spendEntity.setUsername(rs.getString("username"));
            spendEntity.setSpendDate(rs.getDate("spend_date"));
            spendEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
            spendEntity.setAmount(rs.getDouble("amount"));
            spendEntity.setDescription(rs.getString("description"));

            CategoryEntity categoryEntity = new CategoryEntity();
            categoryEntity.setId(rs.getObject("category_id", UUID.class));
            categoryEntity.setUsername(rs.getString("username"));
            categoryEntity.setName(rs.getString("category_name"));
            categoryEntity.setArchived(rs.getBoolean("category_archived"));
            spendEntity.setCategory(categoryEntity);
        }
        return spendEntity;
    }
}
