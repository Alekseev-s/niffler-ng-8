package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.template.XaTransactionTemplate;
import guru.qa.niffler.model.auth.AuthorityValues;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUserEntity = authUserEntity(username, password);
            authUserRepository.create(authUserEntity);
            return UserJson.fromEntity(
                    userdataUserRepository.create(userEntity(username)),
                    null
            );
        });
    }

    @Override
    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(targetUser.id())
                    .orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomDataUtils.getRandomUsername();
                    AuthUserEntity authUserEntity = authUserEntity(username, "12345");
                    authUserRepository.create(authUserEntity);
                    UserEntity addressee = userdataUserRepository.create(userEntity(username));
                    userdataUserRepository.addFriendshipInvitation(targetEntity, addressee);
                    return null;
                });
            }
        }
    }

    @Override
    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(targetUser.id())
                    .orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomDataUtils.getRandomUsername();
                    AuthUserEntity authUserEntity = authUserEntity(username, "12345");
                    authUserRepository.create(authUserEntity);
                    UserEntity addressee = userdataUserRepository.create(userEntity(username));
                    userdataUserRepository.addFriendshipInvitation(addressee, targetEntity);
                    return null;
                });
            }
        }
    }

    @Override
    public void createFriend(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(targetUser.id())
                    .orElseThrow();

            for (int i = 0; i < count; i++) {
               xaTransactionTemplate.execute(() -> {
                   String username = RandomDataUtils.getRandomUsername();
                   AuthUserEntity authUserEntity = authUserEntity(username, "12345");
                   authUserRepository.create(authUserEntity);
                   UserEntity addressee = userdataUserRepository.create(userEntity(username));
                   userdataUserRepository.addFriend(targetEntity, addressee);
                   return null;
               });
            }
        }
    }

    private UserEntity userEntity(String username) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setCurrency(CurrencyValues.RUB);
        return userEntity;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(username);
        authUserEntity.setPassword(pe.encode(password));
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);
        authUserEntity.setAuthorities(
                Arrays.stream(AuthorityValues.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUserEntity);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUserEntity;
    }
}
