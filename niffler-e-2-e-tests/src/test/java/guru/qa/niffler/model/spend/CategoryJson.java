package guru.qa.niffler.model.spend;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

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

    public static CategoryJson fromEntity(CategoryEntity categoryEntity) {
        return new CategoryJson(
                categoryEntity.getId(),
                categoryEntity.getName(),
                categoryEntity.getUsername(),
                categoryEntity.isArchived()
        );
    }
}
