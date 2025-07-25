package guru.qa.niffler.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
public record CategoryJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("name")
    String name,
    @JsonProperty("username")
    String username,
    @JsonProperty("archived")
    boolean archived) {

    public static @Nonnull CategoryJson fromEntity(CategoryEntity categoryEntity) {
        return new CategoryJson(
                categoryEntity.getId(),
                categoryEntity.getName(),
                categoryEntity.getUsername(),
                categoryEntity.isArchived()
        );
    }
}
