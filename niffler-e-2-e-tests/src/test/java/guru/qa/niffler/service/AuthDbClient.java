package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.entity.AuthorityEntity;
import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.AuthorityJson;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;
import static guru.qa.niffler.data.Databases.xaTransaction;

public class AuthDbClient {

    private static final Config CFG = Config.getInstance();

//    public AuthUserJson createUser(AuthUserJson userJson) {
//        return transaction(connection -> {
//            AuthUserEntity userEntity = AuthUserEntity.fromJson(userJson);
//            return AuthUserJson.fromEntity(new AuthUserDaoJdbc(connection).create(userEntity));
//        }, CFG.authJdbcUrl(), 1);
//    }
//
    public AuthorityJson createAuthority(AuthorityJson authorityJson) {
        return transaction(connection -> {
            AuthorityEntity authorityEntity = AuthorityEntity.fromJson(authorityJson);
            return AuthorityJson.fromEntity(new AuthAuthorityDaoJdbc(connection).create(authorityEntity));
        }, CFG.authJdbcUrl(), 1);
    }

    public void createUserWithAuthority(AuthUserJson userJson, AuthorityJson authorityJson) {
        xaTransaction(
                1,
                new Databases.XaConsumer(connection -> {
                    AuthUserEntity userEntity = AuthUserEntity.fromJson(userJson);
                    new AuthUserDaoJdbc(connection).create(userEntity);
                }, CFG.authJdbcUrl()),
                new Databases.XaConsumer(connection -> {
                    AuthorityEntity authorityEntity = AuthorityEntity.fromJson(authorityJson);
                    new AuthAuthorityDaoJdbc(connection).create(authorityEntity);
                }, CFG.authJdbcUrl()));
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
