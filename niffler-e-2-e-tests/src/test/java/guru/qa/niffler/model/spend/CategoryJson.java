package guru.qa.niffler.model.spend;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

public record CategoryJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("name")
    String name,
    @JsonProperty("username")
    String username,
    @JsonProperty("archived")
    boolean archived) {

    public static @Nonnull CategoryJson fromEntity(@Nonnull CategoryEntity categoryEntity) {
        return new CategoryJson(
                categoryEntity.getId(),
                categoryEntity.getName(),
                categoryEntity.getUsername(),
                categoryEntity.isArchived()
        );
    }
}
