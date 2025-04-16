package guru.qa.niffler.model.spend;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.Date;
import java.util.UUID;

public record SpendJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("spendDate")
    Date spendDate,
    @JsonProperty("category")
    CategoryJson category,
    @JsonProperty("currency")
    CurrencyValues currency,
    @JsonProperty("amount")
    Double amount,
    @JsonProperty("description")
    String description,
    @JsonProperty("username")
    String username) {

    public static SpendJson fromEntity(SpendEntity spendEntity) {
        final CategoryEntity categoryEntity = spendEntity.getCategory();

        return new SpendJson(
                spendEntity.getId(),
                spendEntity.getSpendDate(),
                CategoryJson.fromEntity(categoryEntity),
                spendEntity.getCurrency(),
                spendEntity.getAmount(),
                spendEntity.getDescription(),
                spendEntity.getUsername()
        );
    }
}
