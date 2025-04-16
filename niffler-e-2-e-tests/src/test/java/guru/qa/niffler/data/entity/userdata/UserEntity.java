package guru.qa.niffler.data.entity.userdata;

import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Getter
@Setter
public class UserEntity implements Serializable {
    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String firstname;
    private String surname;
    private String fullname;
    private byte[] photo;
    private byte[] photoSmall;

    public static UserEntity fromJson(UserJson userJson) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userJson.id());
        userEntity.setUsername(userJson.username());
        userEntity.setCurrency(userJson.currency());
        userEntity.setFirstname(userJson.firstname());
        userEntity.setSurname(userJson.surname());
        userEntity.setFullname(userJson.fullname());
        userEntity.setPhoto(userJson.photo() != null ? userJson.photo().getBytes(StandardCharsets.UTF_8) : null);
        userEntity.setPhotoSmall(userJson.photoSmall() != null ? userJson.photoSmall().getBytes(StandardCharsets.UTF_8) : null);
        return userEntity;
    }
}
