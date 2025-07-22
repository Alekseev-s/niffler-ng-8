package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.spend.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class StatGraphqlTest extends BaseGraphqlTest {

    @User
    @Test
    @ApiLogin
    void statTest(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        assertEquals(
                0.0,
                result.total
        );
    }

    @User(
            categories = {
                    @Category(name = "Активная категория"),
                    @Category(name = "Архивная категория", archived = true)
            },
            spends = {
                    @Spending(
                            category = "Активная категория",
                            description = "Обучение Advanced 1.0",
                            currency = CurrencyValues.USD,
                            amount = 100
                    ),

                    @Spending(
                            category = "Архивная категория",
                            description = "Обучение Advanced 2.0",
                            currency = CurrencyValues.USD,
                            amount = 150
                    ),
            }
    )
    @ApiLogin
    @Test
    void statShouldReturnArchivedCategories(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(guru.qa.type.CurrencyValues.USD)
                        .statCurrency(guru.qa.type.CurrencyValues.USD)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("Authorization", "Bearer " + bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        final StatQuery.Stat result = data.stat;

        assertEquals(250, result.total);
        assertEquals("Активная категория", result.statByCategories.getFirst().categoryName);
        assertEquals(100, result.statByCategories.getFirst().sum);
        assertEquals("Archived", result.statByCategories.getLast().categoryName);
        assertEquals(150, result.statByCategories.getLast().sum);
    }

    @User(
            categories = {
                    @Category(name = "Развлечения"),
                    @Category(name = "Обучение")
            },
            spends = {
                    @Spending(
                            category = "Развлечения",
                            description = "Кино",
                            amount = 100,
                            currency = CurrencyValues.USD
                    ),
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 150,
                            currency = CurrencyValues.EUR
                    )
            }
    )
    @ApiLogin
    @Test
    void currenciesShouldBeConverted(@Token String bearerToken) {
        final ApolloCall<StatQuery.Data> statCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", "Bearer " + bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(statCall).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        final StatQuery.Stat result = data.stat;

        Assertions.assertEquals(17466.67, result.total);
        Assertions.assertEquals(CurrencyValues.RUB.name(), result.currency.rawValue);
    }
}
