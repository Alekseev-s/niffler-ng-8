package guru.qa.niffler.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import java.util.UUID;

public record AuthorityJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("user")
        AuthUserJson user,
        @JsonProperty("authority")
        AuthorityValues authority
) {
    public static AuthorityJson fromEntity(AuthorityEntity authorityEntity) {
        return new AuthorityJson(
                authorityEntity.getId(),
                AuthUserJson.fromEntity(authorityEntity.getUser()),
                authorityEntity.getAuthority()
        );
    }
}
