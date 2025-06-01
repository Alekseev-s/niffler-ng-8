package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.jdbc.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.template.Connections.holder;

@ParametersAreNonnullByDefault
public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private final UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc();

    @Nonnull
    @Override
    public UserEntity create(UserEntity userEntity) {
        return userdataUserDao.createUser(userEntity);
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findById(UUID id) {
        return userdataUserDao.findById(id);
    }

    @Nonnull
    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userdataUserDao.findByUsername(username);
    }

    @Nonnull
    @Override
    public UserEntity update(UserEntity userEntity) {
        try (PreparedStatement userPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                """
                        UPDATE user
                        SET
                            username = ?,
                            currency = ?,
                            firstname = ?,
                            surname = ?,
                            photo = ?,
                            photo_small = ?,
                            full_name = ?
                        WHERE id = ?
                        """
        );
        PreparedStatement friendPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                """
                        INSERT INTO friendship (
                            requester_id,
                            addressee_id,
                            status
                        ) VALUES (?, ?, ?)
                        ON CONFLICT (requester_id, addressee_id)
                        DO UPDATE SET status = ?
                        """
        )) {
            userPs.setString(1, userEntity.getUsername());
            userPs.setString(2, String.valueOf(userEntity.getCurrency()));
            userPs.setString(3, userEntity.getFirstname());
            userPs.setString(4, userEntity.getSurname());
            userPs.setBytes(5, userEntity.getPhoto());
            userPs.setBytes(6, userEntity.getPhotoSmall());
            userPs.setString(7, userEntity.getFullname());
            userPs.setObject(8, userEntity.getId());
            userPs.executeUpdate();

            for (FriendshipEntity fe : userEntity.getFriendshipRequests()) {
                friendPs.setObject(1, userEntity.getId());
                friendPs.setObject(2, fe.getAddressee().getId());
                friendPs.setString(3, fe.getStatus().name());
                friendPs.setString(4, fe.getStatus().name());
                friendPs.addBatch();
                friendPs.clearParameters();
            }
            friendPs.executeBatch();

            return userEntity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFriendshipInvitation(UserEntity requester, UserEntity addressee) {
        requester.addFriends(FriendshipStatus.PENDING, addressee);
        update(requester);
    }


    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        requester.addFriends(FriendshipStatus.ACCEPTED, addressee);
        addressee.addFriends(FriendshipStatus.ACCEPTED, requester);
        update(addressee);
        update(requester);
    }

    @Override
    public void remove(UserEntity userEntity) {
        try (PreparedStatement friendshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?"
        );
             PreparedStatement userPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     "DELETE FROM \"user\" WHERE id = ?"
             )) {
            friendshipPs.setObject(1, userEntity.getId());
            friendshipPs.setObject(2, userEntity.getId());
            friendshipPs.executeUpdate();

            userPs.setObject(1, userEntity.getId());
            userPs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
