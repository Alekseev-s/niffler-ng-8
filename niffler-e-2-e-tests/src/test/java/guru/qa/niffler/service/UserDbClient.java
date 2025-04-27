package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.jdbc.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.jdbc.UserdataUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.spring.UserdataUserRepositorySpringJdbc;
import guru.qa.niffler.data.template.DataSources;
import guru.qa.niffler.data.template.JdbcTransactionTemplate;
import guru.qa.niffler.data.template.XaTransactionTemplate;
import guru.qa.niffler.model.auth.AuthorityJson;
import guru.qa.niffler.model.auth.AuthorityValues;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authJdbcUrl())
            )
    );

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

    public void addIncomeInvitation(UserJson targetUser, int count) {
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

    public void addOutcomeInvitation(UserJson targetUser, int count) {
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

    public void addFriend(UserJson targetUser, int count) {

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
