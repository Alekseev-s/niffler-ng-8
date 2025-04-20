package guru.qa.niffler.test.web;

import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UserDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

public class JdbcTest {

    @Test
    void jdbcWithoutTxTest() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson user = userDbClient.createUserJdbcWithoutTx(
                new UserJson(
                        null,
                        RandomDataUtils.getRandomUsername(),
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void jdbcWithTxTest() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson user = userDbClient.createUserJdbcWithTx(
                new UserJson(
                        null,
                        RandomDataUtils.getRandomUsername(),
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void springJdbcWithoutTxTest() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson user = userDbClient.createUserSpringJdbcWithoutTx(
                new UserJson(
                        null,
                        RandomDataUtils.getRandomUsername(),
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void springJdbcWithTxTest() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson user = userDbClient.createUserSpringJdbcWithTx(
                new UserJson(
                        null,
                        RandomDataUtils.getRandomUsername(),
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void correctChainedSpringJdbcTest() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson user = userDbClient.createUserChainedTxTemplate(
                new UserJson(
                        null,
                        RandomDataUtils.getRandomUsername(),
                        null,
                        null,
                        null,
                        CurrencyValues.RUB,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    /*
    Доказательство невозможности отката внутренней транзакции:
    В БД niffler-auth в таблицах user и authority создается запись о пользователе и его authorities.
    В БД uiffler-userdata в таблице user запись о пользователе не создается.
    При возникновении ошибки не выполняется откат из niffler-auth
     */
    @Test
    void incorrectChainedSpringJdbcTest() {
        UserDbClient userDbClient = new UserDbClient();
        UserJson user = userDbClient.createUserChainedTxTemplate(
                new UserJson(
                        null,
                        RandomDataUtils.getRandomUsername(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );
        System.out.println(user);
    }
}
