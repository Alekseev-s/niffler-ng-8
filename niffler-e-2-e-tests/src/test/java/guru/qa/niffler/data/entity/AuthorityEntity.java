package guru.qa.niffler.data.entity;

import guru.qa.niffler.model.AuthorityJson;
import guru.qa.niffler.model.AuthorityValues;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class AuthorityEntity implements Serializable {
    private UUID id;
    private AuthUserEntity user;
    private AuthorityValues authority;

    public static AuthorityEntity fromJson(AuthorityJson authorityJson) {
        AuthorityEntity authorityEntity = new AuthorityEntity();
        authorityEntity.setId(authorityJson.id());
        authorityEntity.setUser(AuthUserEntity.fromJson(authorityJson.user()));
        authorityEntity.setAuthority(authorityJson.authority());
        return authorityEntity;
    }
}
