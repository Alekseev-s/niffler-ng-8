package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserdataUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.auth.AuthorityJson;
import guru.qa.niffler.model.auth.AuthorityValues;
import guru.qa.niffler.model.userdata.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;
import static guru.qa.niffler.data.Databases.xaTransaction;

public class UserDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserJson createUser(UserJson userJson) {
        return UserJson.fromEntity(
                xaTransaction(
                        1,
                        new Databases.XaFunction<>(
                                connection -> {
                                    AuthUserEntity authUserEntity = new AuthUserEntity();
                                    authUserEntity.setUsername(userJson.username());
                                    authUserEntity.setPassword(pe.encode("12345"));
                                    authUserEntity.setEnabled(true);
                                    authUserEntity.setAccountNonExpired(true);
                                    authUserEntity.setAccountNonLocked(true);
                                    authUserEntity.setCredentialsNonExpired(true);
                                    new AuthUserDaoJdbc(connection).create(authUserEntity);
                                    new AuthAuthorityDaoJdbc(connection).create(
                                            Arrays.stream(AuthorityValues.values())
                                                    .map(a -> {
                                                        AuthorityEntity authorityEntity = new AuthorityEntity();
                                                        authorityEntity.setUserId(authUserEntity.getId());
                                                        authorityEntity.setAuthority(a);
                                                        return authorityEntity;
                                                    }).toArray(AuthorityEntity[]::new));
                                    return null;
                                }, CFG.authJdbcUrl()
                        ),
                        new Databases.XaFunction<>(
                                connection -> {
                                    UserEntity userEntity = UserEntity.fromJson(userJson);
                                    userEntity = new UserdataUserDaoJdbc(connection).createUser(userEntity);
                                    return userEntity;
                                }, CFG.userdataJdbcUrl()
                        )
                )
        );
    }

    public AuthorityJson findAuthorityById(UUID id) {
        return transaction(connection -> {
            Optional<AuthorityEntity> authorityOpt = new AuthAuthorityDaoJdbc(connection).findAuthorityById(id);
            if (authorityOpt.isEmpty()) {
                throw new RuntimeException("Authority not found");
            }
            AuthorityEntity authorityEntity = authorityOpt.get();
            if (authorityEntity.getUserId() != null) {
                Optional<AuthUserEntity> userOpt = new AuthUserDaoJdbc(connection).findUserById(authorityEntity.getUserId());
                if (userOpt.isEmpty()) {
                    throw new RuntimeException("User not found");
                }
                authorityEntity.setUser(userOpt.get());
            }
            return AuthorityJson.fromEntity(authorityEntity);
        }, CFG.authJdbcUrl(), 1);
    }
}
