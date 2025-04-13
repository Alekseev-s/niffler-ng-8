package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.AuthUserEntity;
import guru.qa.niffler.data.entity.AuthorityEntity;
import guru.qa.niffler.model.AuthUserJson;
import guru.qa.niffler.model.AuthorityJson;

import static guru.qa.niffler.data.Databases.transaction;

public class AuthDbClient {

    private static final Config CFG = Config.getInstance();

    public AuthUserJson createUser(AuthUserJson userJson) {
        return transaction(connection -> {
            AuthUserEntity userEntity = AuthUserEntity.fromJson(userJson);
            return AuthUserJson.fromEntity(new AuthUserDaoJdbc(connection).create(userEntity));
        }, CFG.authJdbcUrl(), 1);
    }

    public AuthorityJson createAuthority(AuthorityJson authorityJson) {
        return transaction(connection -> {
            AuthorityEntity authorityEntity = AuthorityEntity.fromJson(authorityJson);
            return AuthorityJson.fromEntity(new AuthAuthorityDaoJdbc(connection).create(authorityEntity));
        }, CFG.authJdbcUrl(), 1);
    }
}
