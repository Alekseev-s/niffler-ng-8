package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDao;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.template.DataSources;
import guru.qa.niffler.data.template.JdbcTransactionTemplate;
import guru.qa.niffler.data.template.XaTransactionTemplate;
import guru.qa.niffler.model.auth.AuthorityJson;
import guru.qa.niffler.model.auth.AuthorityValues;
import guru.qa.niffler.model.userdata.UserJson;
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

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();
    private final UserdataUserDao userdataUserDao = new UserdataUserDaoJdbc();

    private final AuthUserDao authUserSpringDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthoritySpringDao = new AuthAuthorityDaoSpringJdbc();
    private final UserdataUserDao userdataUserSpringDao = new UserdataUserDaoSpringJdbc();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(DataSources.dataSource(CFG.authJdbcUrl()))
    );

    private final TransactionTemplate chainedTxTemplate = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(
                            DataSources.simpleDataSource(CFG.authJdbcUrl())),
                    new JdbcTransactionManager(
                            DataSources.simpleDataSource(CFG.userdataJdbcUrl())
                    )
            )
    );

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(CFG.spendJdbcUrl());

    public UserJson createUserJdbcWithoutTx(UserJson userJson) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(userJson.username());
        authUserEntity.setPassword(pe.encode("12345"));
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUserEntity = authUserDao.create(authUserEntity);
        AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUserEntity);

        authAuthorityDao.create(authorityEntities);

        return UserJson.fromEntity(
                userdataUserDao.createUser(UserEntity.fromJson(userJson)),
                null
        );
    }

    public UserJson createUserJdbcWithTx(UserJson userJson) {
        return xaTxTemplate.execute(() -> {
            AuthUserEntity authUserEntity = new AuthUserEntity();
            authUserEntity.setUsername(userJson.username());
            authUserEntity.setPassword(pe.encode("12345"));
            authUserEntity.setEnabled(true);
            authUserEntity.setAccountNonExpired(true);
            authUserEntity.setAccountNonLocked(true);
            authUserEntity.setCredentialsNonExpired(true);

            AuthUserEntity createdAuthUserEntity = authUserDao.create(authUserEntity);
            AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUserEntity);

            authAuthorityDao.create(authorityEntities);

            return UserJson.fromEntity(
                    userdataUserDao.createUser(UserEntity.fromJson(userJson)),
                    null
            );
        });
    }

    public UserJson createUserSpringJdbcWithoutTx(UserJson userJson) {
        AuthUserEntity authUserEntity = new AuthUserEntity();
        authUserEntity.setUsername(userJson.username());
        authUserEntity.setPassword(pe.encode("12345"));
        authUserEntity.setEnabled(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUserEntity = authUserSpringDao.create(authUserEntity);
        AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUserEntity);

        authAuthoritySpringDao.create(authorityEntities);

        return UserJson.fromEntity(
                userdataUserSpringDao.createUser(UserEntity.fromJson(userJson)),
                null
        );
    }

    public UserJson createUserSpringJdbcWithTx(UserJson userJson) {
        return txTemplate.execute(status -> {
            AuthUserEntity authUserEntity = new AuthUserEntity();
            authUserEntity.setUsername(userJson.username());
            authUserEntity.setPassword(pe.encode("12345"));
            authUserEntity.setEnabled(true);
            authUserEntity.setAccountNonExpired(true);
            authUserEntity.setAccountNonLocked(true);
            authUserEntity.setCredentialsNonExpired(true);

            AuthUserEntity createdAuthUserEntity = authUserSpringDao.create(authUserEntity);
            AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUserEntity);

            authAuthoritySpringDao.create(authorityEntities);

            return UserJson.fromEntity(
                    userdataUserSpringDao.createUser(UserEntity.fromJson(userJson)),
                    null
            );
        });
    }

    public UserJson createUserChainedTxTemplate(UserJson userJson) {
        return chainedTxTemplate.execute(status -> {
            AuthUserEntity authUserEntity = new AuthUserEntity();
            authUserEntity.setUsername(userJson.username());
            authUserEntity.setPassword(pe.encode("12345"));
            authUserEntity.setEnabled(true);
            authUserEntity.setAccountNonExpired(true);
            authUserEntity.setAccountNonLocked(true);
            authUserEntity.setCredentialsNonExpired(true);

            AuthUserEntity createdAuthUserEntity = authUserDao.create(authUserEntity);
            AuthorityEntity[] authorityEntities = getAuthorityEntities(createdAuthUserEntity);

            authAuthorityDao.create(authorityEntities);

            return UserJson.fromEntity(
                    userdataUserDao.createUser(UserEntity.fromJson(userJson)),
                    null
            );
        });
    }

    public AuthorityJson findAuthorityById(UUID id) {
        return jdbcTxTemplate.execute(() -> {
            Optional<AuthorityEntity> authorityOpt = authAuthorityDao.findAuthorityById(id);
            if (authorityOpt.isEmpty()) {
                throw new RuntimeException("Authority not found");
            }
            AuthorityEntity authorityEntity = authorityOpt.get();
            if (authorityEntity.getUser() != null) {
                Optional<AuthUserEntity> userOpt = authUserDao.findUserById(authorityEntity.getUser().getId());
                if (userOpt.isEmpty()) {
                    throw new RuntimeException("User not found");
                }
                authorityEntity.setUser(userOpt.get());
            }
            return AuthorityJson.fromEntity(authorityEntity);
        }, 1);
    }

    public AuthorityEntity[] getAuthorityEntities(AuthUserEntity userEntity) {
        return Arrays.stream(AuthorityValues.values()).map(
                a -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(userEntity);
                    ae.setAuthority(a);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);
    }
}
