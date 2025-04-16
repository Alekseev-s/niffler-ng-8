package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.auth.AuthUserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class AuthUserEntity implements Serializable {
    private UUID id;
    private String username;
    private String password;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    public static AuthUserEntity fromJson(AuthUserJson userJson) {
        AuthUserEntity userEntity = new AuthUserEntity();
        userEntity.setId(userJson.id());
        userEntity.setUsername(userEntity.getUsername());
        userEntity.setPassword(userEntity.getPassword());
        userEntity.setEnabled(userEntity.isEnabled());
        userEntity.setAccountNonExpired(userEntity.isAccountNonExpired());
        userEntity.setAccountNonLocked(userEntity.isAccountNonLocked());
        userEntity.setCredentialsNonExpired(userEntity.isCredentialsNonExpired());
        return userEntity;
    }
}
