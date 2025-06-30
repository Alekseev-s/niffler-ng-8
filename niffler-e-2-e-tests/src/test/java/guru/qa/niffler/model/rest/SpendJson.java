package guru.qa.niffler.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.spend.CurrencyValues;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Date;
import java.util.UUID;

@ParametersAreNonnullByDefault
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

    public static @Nonnull SpendJson fromEntity(SpendEntity spendEntity) {
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
