package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {
    UserEntity create(UserEntity userEntity);

    Optional<UserEntity> findById(UUID id);

    void addFriendshipInvitation(UserEntity requester, UserEntity addressee);


    void addFriend(UserEntity requester, UserEntity addressee);
}
