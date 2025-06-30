package guru.qa.niffler.model.userdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.spend.CurrencyValues;
import org.openqa.selenium.devtools.v130.layertree.model.Layer;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("surname")
        String surname,
        @JsonProperty("fullname")
        String fullname,
        @JsonProperty("currency")
        CurrencyValues currency,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("photoSmall")
        String photoSmall,
        @JsonProperty("friendshipStatus")
        FriendshipStatus friendshipStatus,
        @JsonIgnore
        TestData testData
) {
    public static @Nonnull UserJson fromEntity(@Nonnull UserEntity userEntity, FriendshipStatus friendshipStatus) {
        return new UserJson(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getFirstname(),
                userEntity.getSurname(),
                userEntity.getFullname(),
                userEntity.getCurrency(),
                userEntity.getPhoto() != null && userEntity.getPhoto().length > 0 ? new String(userEntity.getPhoto(), StandardCharsets.UTF_8) : null,
                userEntity.getPhotoSmall() != null && userEntity.getPhotoSmall().length > 0 ? new String(userEntity.getPhotoSmall(), StandardCharsets.UTF_8) : null,
                friendshipStatus,
                new TestData(
                        null,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
    }

    public UserJson(String username) {
        this(username, null);
    }

    public UserJson(String username, TestData testData) {
        this(null, username, null, null, null, null, null, null, null, testData);
    }

    public @Nonnull UserJson withPassword(@Nonnull String password) {
        return withTestData(
                new TestData(
                        password,
                        testData.categories(),
                        testData.spendings(),
                        testData.incomeInvitations(),
                        testData.outcomeInvitations(),
                        testData.friends()
                )
        );
    }

    public @Nonnull UserJson withTestData(@Nonnull TestData testData) {
        return new UserJson(
                id,
                username,
                firstname,
                surname,
                fullname,
                currency,
                photo,
                photoSmall,
                friendshipStatus,
                testData
        );
    }

    public UserJson withUsers(
            List<UserJson> friends,
            List<UserJson> outcomeInvitations,
            List<UserJson> incomeInvitations
    ) {
        return withTestData(
                new TestData(
                        testData.password(),
                        testData.categories(),
                        testData.spendings(),
                        friends,
                        outcomeInvitations,
                        incomeInvitations
                )
        );
    }
}
