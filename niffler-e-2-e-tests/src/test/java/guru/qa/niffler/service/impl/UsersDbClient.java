package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
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
import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Step("Create user")
    @Nonnull
    public UserJson createUser(String username, String password) {
        return requireNonNull(xaTransactionTemplate.execute(() -> {
            AuthUserEntity authUserEntity = authUserEntity(username, password);
            authUserRepository.create(authUserEntity);
            return UserJson.fromEntity(
                    userdataUserRepository.create(userEntity(username)),
                    null
            );
        }));
    }

    @Step("Create income invitation")
    @Override
    public List<UserJson> createIncomeInvitations(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(targetUser.id())
                    .orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomDataUtils.getRandomUsername();
                    UserEntity income = createNewUser(username, "12345");
                    result.add(UserJson.fromEntity(income, FriendshipStatus.INVITE_RECEIVED));
                    userdataUserRepository.addFriendshipInvitation(targetEntity, income);
                    return null;
                });
            }
        }
        return result;
    }

    @Step("Create outcome invitation")
    @Override
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(targetUser.id())
                    .orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomDataUtils.getRandomUsername();
                    UserEntity outcome = createNewUser(username, "12345");
                    result.add(UserJson.fromEntity(outcome, FriendshipStatus.INVITE_SENT));
                    userdataUserRepository.addFriendshipInvitation(outcome, targetEntity);
                    return null;
                });
            }
        }
        return result;
    }

    @Step("Create friendship")
    @Override
    public List<UserJson> createFriend(UserJson targetUser, int count) {
        final List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findById(targetUser.id())
                    .orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = RandomDataUtils.getRandomUsername();
                    UserEntity friend = createNewUser(username, "12345");
                    result.add(UserJson.fromEntity(friend, FriendshipStatus.FRIEND));
                    userdataUserRepository.addFriend(targetEntity, friend);
                    return null;
                });
            }
        }
        return result;
    }

    @Nonnull
    private UserEntity createNewUser(String username, String password) {
        AuthUserEntity authUser = authUserEntity(username, password);
        authUserRepository.create(authUser);
        return userdataUserRepository.create(userEntity(username));
    }

    @Nonnull
    private UserEntity userEntity(String username) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setCurrency(CurrencyValues.RUB);
        return userEntity;
    }

    @Nonnull
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
