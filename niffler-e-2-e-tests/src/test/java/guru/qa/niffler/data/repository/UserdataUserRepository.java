package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserdataUserRepository {
    @Nonnull
    UserEntity create(UserEntity userEntity);

    @Nonnull
    Optional<UserEntity> findById(UUID id);

    @Nonnull
    Optional<UserEntity> findByUsername(String username);

    @Nonnull
    UserEntity update(UserEntity userEntity);

    void addFriendshipInvitation(UserEntity requester, UserEntity addressee);

    void addFriend(UserEntity requester, UserEntity addressee);

    void remove(UserEntity userEntity);
}
