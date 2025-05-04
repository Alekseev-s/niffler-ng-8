package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.spring.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.template.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    private final UserdataUserDao userdataUserDao = new UserdataUserDaoSpringJdbc();

    @Override
    public UserEntity create(UserEntity userEntity) {
        return userdataUserDao.createUser(userEntity);
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        return userdataUserDao.findById(id);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userdataUserDao.findByUsername(username);
    }

    @Override
    public UserEntity update(UserEntity userEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    """
                            UPDATE "user"
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
            ps.setString(1, userEntity.getUsername());
            ps.setString(2, String.valueOf(userEntity.getCurrency()));
            ps.setString(3, userEntity.getFirstname());
            ps.setString(4, userEntity.getSurname());
            ps.setBytes(5, userEntity.getPhoto());
            ps.setBytes(6, userEntity.getPhotoSmall());
            ps.setString(7, userEntity.getFullname());
            ps.setObject(8, userEntity.getId());
            return ps;
        });

        List<FriendshipEntity> friendshipEntities = userEntity.getFriendshipRequests();
        jdbcTemplate.batchUpdate(
                """
                        INSERT INTO friendship (
                            requester_id,
                            addressee_id,
                            status
                        ) VALUES (?, ?, ?)
                        ON CONFLICT (requester_id, addressee_id)
                        DO UPDATE SET status = ?
                        """,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, userEntity.getId());
                        ps.setObject(2, friendshipEntities.get(i).getAddressee().getId());
                        ps.setString(3, friendshipEntities.get(i).getStatus().name());
                        ps.setString(4, friendshipEntities.get(i).getStatus().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return friendshipEntities.size();
                    }
                }
        );

        return userEntity;
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
        update(requester);
        update(addressee);
    }

    @Override
    public void remove(UserEntity userEntity) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(
                "DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?",
                userEntity.getId(),
                userEntity.getId()
        );
        jdbcTemplate.update("DELETE FROM \"user\" WHERE id = ?", userEntity.getId());
    }
}
